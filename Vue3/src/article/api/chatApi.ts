// src/article/api/chatApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { ChatVO } from '@/common/types/vo/ChatVO';
import { myFetch } from '@/common/utils/fetch';
import type { AxiosResponse } from 'axios';

export const chatApi = {
    /**
     * 与文章对话（SSE流式响应）
     * @param articleId 文章ID
     * @param article 文章内容
     * @param prompt 用户提问
     * @param title 文章标题
     */
    chatWithArticle(
        articleId: number,
        article: string,
        prompt: string,
        title: string
    ): Promise<Response> {
        return myFetch({
            method: "POST",
            url: '/article/read/chat',
            params: {
                articleId,
                prompt,
                title,
            },
            data: {
                article,
            },
        });
    },

    /**
     * 删除所有聊天记录
     */
    deleteAllChats(): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: '/article/read',
        });
    },

    /**
     * 根据文章ID删除聊天记录
     * @param articleId 文章ID
     */
    deleteChatsByArticleId(
        articleId: number
    ): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/article/read/articleId/${articleId}`,
        });
    },

    /**
     * 根据ID删除聊天记录
     * @param id 聊天记录ID
     */
    deleteChatsById(
        id: number
    ): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/article/read/${id}`,
        });
    },

    /**
     * 根据文章ID获取聊天记录
     * @param articleId 文章ID
     */
    getChatsByArticleId(
        articleId: number
    ): Promise<AxiosResponse<Result<ChatVO[]>>> {
        return myAxios({
            method: 'get',
            url: `/article/read/articleId/${articleId}`,
        });
    },

    /**
     * 根据ID获取单条聊天记录
     * @param id 记录ID
     */
    getChatById(id: number): Promise<AxiosResponse<Result<ChatVO>>> {
        return myAxios({
            method: 'get',
            url: `/article/read/${id}`,
        });
    },

    /**
     * 下载聊天记录（MD格式）
     * @param id 文章ID
     */
    downloadChat(id: number): Promise<AxiosResponse<Blob>> {
        return myAxios({
            method: "get",
            url: `/article/read/download/${id}`,
            responseType: 'blob'
        })
    },
};