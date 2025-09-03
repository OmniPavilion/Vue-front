// api/aiChatApi.ts
import { myFetch } from '@/common/utils/fetch';

export const englishAiApi = {
    /**
     * 添加单词ai对话接口（SSE流式响应）
     * @param prompt 用户提问
     */
    addWord(prompt: string): Promise<Response> {
        return myFetch({
            method: 'POST',
            url: '/english/ai/addWord',
            params: { prompt },
        });
    },

    /**
     * 翻译对话接口（SSE流式响应）
     * @param prompt 用户提问（需要翻译的内容）
     */
    translate(prompt: string): Promise<Response> {
        return myFetch({
            method: 'POST',
            url: '/english/ai/translate',
            params: { prompt },
        });
    }
};