import { defineStore } from 'pinia'
import { ref } from 'vue'
import { aiReadApi } from '@/bibliophile/api/readerApi'
import type { ChatVO } from '@/common/types/vo/ChatVO'
import logger from '@/common/utils/logger'
import type {AiReaderFile} from "@/bibliophile/types/vo/AiReaderFile";

export const useAiReadStore = defineStore('AiReadStore', () => {
    const currentFile = ref<AiReaderFile | null>(null) // 当前文件ID
    const chatMessages = ref<ChatVO[]>([]) // 聊天消息列表
    const isLoading = ref<boolean>(false) // 加载状态
    const isStreaming = ref<boolean>(false) // 流式响应状态
    const error = ref<string | null>(null) // 错误信息

    /**
     * 与文件对话（SSE流式响应）
     * @param prompt - 用户提问
     */
    const chatWithFile = async (prompt: string) => {
        logger.info('正在与文件对话...')
        if (isLoading.value || isStreaming.value) {
            logger.log("已有请求正在处理中")
            return
        }

        if (!currentFile.value) {
            logger.log("请选择文件")
            return
        }

        if (!currentFile.value.id) {
            logger.log("请选择文件")
            return
        }


        // 添加用户消息
        addUserMessage(prompt)

        isStreaming.value = true
        error.value = null

        try {
            const response = await aiReadApi.readFile(prompt, currentFile.value?.id)

            if (!response.ok) {
                logger.error("文件对话失败:", response.status, response.statusText)
                return
            }

            if (!response.body) {
                logger.error("文件对话失败:", "响应体为空")
                return
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
            logger.error("文件对话失败:", err)
            error.value = "AI服务暂时不可用，请稍后再试"
            addSystemMessage(error.value)
        } finally {
            isStreaming.value = false
        }
    }

    /**
     * 加载聊天历史记录
     */
    const loadChatHistory = async () => {
        logger.info("加载聊天历史记录...")

        if (!currentFile.value?.id) {
            logger.log("请选择文件")
            return
        }

        isLoading.value = true
        try {
            const response = await aiReadApi.getChatsByFileId(currentFile.value?.id)
            chatMessages.value = response.data.data || []
        } catch (err) {
            logger.error("加载聊天记录失败:", err)
            error.value = "加载聊天记录失败"
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 删除当前文件的所有聊天记录
     */
    const deleteChatHistory = async () => {
        logger.log("删除当前文件的所有聊天记录")

        if (!currentFile.value?.id) return {
            code: -1,
            message: "未选择文件",
            data: null
        }

        isLoading.value = true
        try {
            const res = await aiReadApi.deleteChatsByFileId(currentFile.value?.id)
            if (res.data.code === 1) {
                chatMessages.value = []
            }
            return res.data
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 根据ID删除聊天记录
     */
    const deleteChatById = async (id: number) => {
        logger.log("删除聊天记录", id)
        isLoading.value = true
        try {
            const res = await aiReadApi.deleteChatById(id)
            if (res.data.code === 1) {
                chatMessages.value = chatMessages.value.filter(msg => msg.id !== id)
            }
            return res.data
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 删除所有聊天记录
     */
    const deleteAllChats = async () => {
        logger.log("删除所有聊天记录")
        isLoading.value = true
        try {
            const res = await aiReadApi.deleteAllChats()
            if (res.data.code === 1) {
                chatMessages.value = []
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
        if (!currentFile.value) return

        if (!currentFile.value.id) return

        try {
            const response = await aiReadApi.downloadChat(currentFile.value.id)

            // 创建下载链接
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', `chat_${currentFile.value.id}_${new Date().toISOString().split('T')[0]}.md`)
            document.body.appendChild(link)
            link.click()

            // 清理
            window.URL.revokeObjectURL(url)
            document.body.removeChild(link)

            logger.log('聊天记录下载成功')
        } catch (err) {
            logger.error("下载聊天记录失败:", err)
            error.value = "下载聊天记录失败"
        }
    }

    /**
     * 清空向量数据库
     */
    const clearVectorStore = async () => {
        logger.log("清空向量数据库")
        isLoading.value = true
        try {
            const res = await aiReadApi.deleteVectorStore()
            return res.data
        } catch (err) {
            logger.error("清空向量数据库失败:", err)
            error.value = "清空向量数据库失败"
            throw err
        } finally {
            isLoading.value = false
        }
    }

    /**
     * 获取特定聊天记录
     */
    const getChatById = async (id: number) => {
        logger.log("获取聊天记录", id)
        isLoading.value = true
        try {
            const res = await aiReadApi.getChatById(id)
            return res.data
        } finally {
            isLoading.value = false
        }
    }

    // 辅助方法：添加用户消息
    const addUserMessage = (content: string) => {
        chatMessages.value.push({
            id: Date.now(),
            conversationId: currentFile.value?.id?.toString() || '',
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
            conversationId: currentFile.value?.id?.toString() || '',
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
            conversationId: currentFile.value?.id?.toString() || '',
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
        chatMessages,
        isLoading,
        isStreaming,
        error,
        currentFile,
        chatWithFile,
        loadChatHistory,
        deleteChatHistory,
        deleteChatById,
        deleteAllChats,
        downloadChatHistory,
        clearVectorStore,
        getChatById,
    }
})