// stores/english/aiChatStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { englishAiApi } from '@/english/api/aiChatApi';
import logger from '@/common/utils/logger';
import {markDownIt} from '@/common/utils/tools';

type ChatMode = 'ADD_WORD' | 'TRANSLATE';

interface ChatMessage {
    id: number;
    content: string;
    type: 'USER' | 'ASSISTANT' | 'SYSTEM';
    timestamp: string;
    mode: 'ADD_WORD' | 'TRANSLATE';
}

export const useAiChatStore = defineStore('aiChat', () => {
    const chatMessages = ref<ChatMessage[]>([]);
    const isLoading = ref<boolean>(false);
    const isStreaming = ref<boolean>(false);
    const error = ref<string | null>(null);
    const isOpen = ref<boolean>(false);

    /**
     * AI聊天函数
     * @param prompt - 用户输入
     * @param mode - 聊天模式：ADD_WORD（添加单词）或 TRANSLATE（翻译）
     * @returns 如果是翻译模式，返回翻译内容；否则返回void
     */
    const chat = async (prompt: string, mode: ChatMode = 'ADD_WORD'): Promise<string | void> => {
        logger.info(`AI聊天 - 模式: ${mode}, 内容:`, prompt);

        if (isLoading.value || isStreaming.value) {
            const errorMsg = "已有请求正在处理中";
            logger.log(errorMsg);
            throw new Error(errorMsg);
        }

        // 添加用户消息到历史记录（仅限添加单词模式）
        if (mode === 'ADD_WORD') {
            addUserMessage(prompt, mode);
        }

        isStreaming.value = true;
        error.value = null;

        let response: Response | null = null;
        let shouldThrowError = false;
        let errorMessage = '';

        try {
            // 根据模式选择不同的API
            if (mode === 'ADD_WORD') {
                response = await englishAiApi.addWord(prompt);
            } else {
                response = await englishAiApi.translate(prompt);
            }

            if (!response.ok) {
                shouldThrowError = true;
                errorMessage = `请求失败: ${response.status}`;
                return;
            }

            if (!response.body) {
                shouldThrowError = true;
                errorMessage = "没有返回内容";
                return;
            }

            let assistantMessage = '';
            const decoder = new TextDecoder();
            const reader = response.body.getReader();

            // 如果是翻译模式，直接收集内容返回
            if (mode === 'TRANSLATE') {
                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;
                    assistantMessage += decoder.decode(value);
                }
                return assistantMessage;
            }

            // 如果是添加单词模式，处理流式响应并存储到store
            if (mode === 'ADD_WORD') {
                // 先添加一个空的助手消息占位
                const messageId = addAssistantMessage('', mode);

                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;

                    assistantMessage += decoder.decode(value);
                    // 更新最后一条助手消息
                    updateAssistantMessage(messageId, assistantMessage);
                }
            }

        } catch (err) {
            const errorMsg = `${mode}对话失败: ${err instanceof Error ? err.message : String(err)}`;
            logger.error(errorMsg, err);
            error.value = "AI服务暂时不可用，请稍后再试";

            // 如果是添加单词模式，添加错误消息到历史记录
            if (mode === 'ADD_WORD') {
                addSystemMessage(error.value, mode);
            }

            throw new Error(errorMsg);
        } finally {
            isStreaming.value = false;

            // 在finally中检查是否需要抛出错误
            if (shouldThrowError && errorMessage) {
                const errorMsg = `${mode}对话失败: ${errorMessage}`;
                logger.error(errorMsg);
                error.value = "AI服务请求失败";

                if (mode === 'ADD_WORD') {
                    addSystemMessage(error.value, mode);
                }
            }
        }
    }

    /**
     * 清空聊天记录
     */
    const clearChat = () => {
        chatMessages.value = [];
        error.value = null;
    }

    /**
     * 清空错误信息
     */
    const clearError = () => {
        error.value = null;
    }

    const openChat = () => {
        isOpen.value = true
    }

    const closeChat = () => {
        isOpen.value = false
    }

    /**
     * 获取添加单词模式的聊天记录
     */
    const getWordChatMessages = () => {
        return chatMessages.value.filter(msg => msg.mode === 'ADD_WORD');
    }

    // 辅助方法：添加用户消息
    const addUserMessage = (content: string, mode: ChatMode) => {
        chatMessages.value.push({
            id: Date.now(),
            content,
            type: 'USER',
            timestamp: new Date().toISOString(),
            mode
        });
    }

    // 辅助方法：添加助手消息（返回消息ID用于更新）
    const addAssistantMessage = (content: string, mode: ChatMode): number => {
        const messageId = Date.now();
        chatMessages.value.push({
            id: messageId,
            content,
            type: 'ASSISTANT',
            timestamp: new Date().toISOString(),
            mode
        });
        return messageId;
    }

    // 辅助方法：添加系统消息
    const addSystemMessage = (content: string, mode: ChatMode) => {
        chatMessages.value.push({
            id: Date.now(),
            content,
            type: 'SYSTEM',
            timestamp: new Date().toISOString(),
            mode
        });
    }

    // 辅助方法：更新助手消息
    const updateAssistantMessage = (messageId: number, content: string) => {
        const message = chatMessages.value.find(m => m.id === messageId);
        if (message && message.type === 'ASSISTANT') {
            message.content =   markDownIt.render(content);
        }
    }

    return {
        chatMessages,
        isLoading,
        isStreaming,
        isOpen,
        chat,
        clearChat,
        clearError,
        getWordChatMessages,
        openChat,
        closeChat,
    };
});