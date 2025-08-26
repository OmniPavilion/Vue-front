import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatApi } from '@/article/api/chatApi'
import type { ChatVO } from '@/common/types/vo/ChatVO'
import logger from '@/common/utils/logger'

export const useReadStore = defineStore('ReadStore', () => {
    const currentArticleId = ref<number | null>(null) // 当前文章ID
    const chatMessages = ref<ChatVO[]>([]) // 聊天消息列表
    const isLoading = ref<boolean>(false) // 加载状态
    const isStreaming = ref<boolean>(false) // 流式响应状态
    const error = ref<string | null>(null) // 错误信息

    /**
     * 与文章对话（SSE流式响应）
     * @param articleId - 文章ID
     * @param articleContent - 文章内容
     * @param prompt - 用户提问
     * @param title - 文章标题
     */
    const chatWithArticle = async (articleId: number, articleContent: string, prompt: string, title: string) => {
        logger.info('正在与文章对话...', articleId)
        if (isLoading.value || isStreaming.value) {
            logger.log("已有请求正在处理中")
            return
        }

        // 设置当前文章ID（如果是新文章）
        if (currentArticleId.value !== articleId) {
            currentArticleId.value = articleId
            await loadChatHistory(articleId)
        }

        // 添加用户消息
        addUserMessage(prompt)

        isStreaming.value = true
        error.value = null

        try {
            const response = await chatApi.chatWithArticle(articleId, articleContent, prompt, title)

            if (!response.ok) {
                throw new Error(`请求失败: ${response.status}`)
            }

            if (!response.body) {
                throw new Error("没有返回内容")
            }

            let assistantMessage = ''
            const decoder = new TextDecoder()
            const reader = response.body.getReader()

            // 先添加一个空的助手消息占位
            const messageId = addAssistantMessage('')

            while (true) {
                const { done, value } = await reader.read()
                if (done) break

                assistantMessage += decoder.decode(value)
                // 更新最后一条助手消息

                updateAssistantMessage(messageId, assistantMessage)
            }
        } catch (err) {
            logger.error("文章对话失败:", err)
            error.value = "AI服务暂时不可用，请稍后再试"
            addSystemMessage(error.value)
        } finally {
            isStreaming.value = false
        }
    }

    /**
     * 加载聊天历史记录
     * @param articleId - 文章ID
     */
    const loadChatHistory = async (articleId: number) => {
        currentArticleId.value = articleId
        logger.info("加载聊天历史记录...", articleId)
        isLoading.value = true
        try {
            const response = await chatApi.getChatsByArticleId(articleId)
            chatMessages.value = response.data.data || []
        } catch (err) {
            logger.error("加载聊天记录失败:", err)
            error.value = "加载聊天记录失败"
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 删除当前文章的所有聊天记录
     */
    const deleteChatHistory = async () => {
        logger.log("删除当前文章的所有聊天记录", currentArticleId.value)

        if (!currentArticleId.value) return {
            code: -1,
            message: "未选择文章",
            data: null
        }

        isLoading.value = true
        try {
            const res = await chatApi.deleteChatsByArticleId(currentArticleId.value)
            if (res.data.code === 1) {
                chatMessages.value = []
            }
            return res.data
        } finally {
            isLoading.value = false
        }
    }

    const deleteChatsById = async (id: number) => {
        logger.log("删除聊天记录", id)
        isLoading.value = true
        try {
            const res = await chatApi.deleteChatsById(id)
            if (res.data.code === 1) {
                chatMessages.value = chatMessages.value.filter(msg => msg.id !== id)
            }
            return res.data
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 下载聊天记录（MD格式）
     */
    const downloadChatHistory = async () => {
        logger.info("下载聊天记录")
        if (!currentArticleId.value) return

        try {
            const response = await chatApi.downloadChat(currentArticleId.value)
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', `chat_${currentArticleId.value}_${new Date().toISOString().split('T')[0]}_${new Date().getMilliseconds()}.md`)
            document.body.appendChild(link)
            link.click()
            link.remove()
        } catch (err) {
            logger.error("下载聊天记录失败:", err)
            error.value = "下载聊天记录失败"
        }
    }

    // 辅助方法：添加用户消息
    const addUserMessage = (content: string) => {
        chatMessages.value.push({
            id: Date.now(),
            conversationId: currentArticleId.value?.toString() || '',
            content,
            type: 'USER',
            timestamp: new Date().toISOString()
        })
    }

    // 辅助方法：添加助手消息（返回消息ID用于更新）
    const addAssistantMessage = (content: string): number => {
        const messageId = Date.now()
        chatMessages.value.push({
            id: messageId,
            conversationId: currentArticleId.value?.toString() || '',
            content,
            type: 'ASSISTANT',
            timestamp: new Date().toISOString()
        })
        return messageId
    }

    // 辅助方法：添加系统消息
    const addSystemMessage = (content: string) => {
        chatMessages.value.push({
            id: Date.now(),
            conversationId: currentArticleId.value?.toString() || '',
            content,
            type: 'SYSTEM',
            timestamp: new Date().toISOString()
        })
    }



    // 辅助方法：更新助手消息
    const updateAssistantMessage = (messageId: number, content: string) => {
        const message = chatMessages.value.find(m => m.id === messageId)
        if (message && message.type === 'ASSISTANT') {
            message.content = content
        }
    }

    return {
        currentArticleId,
        chatMessages,
        isLoading,
        isStreaming,
        error,
        chatWithArticle,
        loadChatHistory,
        deleteChatHistory,
        downloadChatHistory,
        deleteChatsById
    }
})