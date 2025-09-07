import type {AxiosResponse} from "axios";
import {myAxios} from "@/common/utils/axios";
import type {Result} from "@/common/types/vo/Result";
import type {ChatVO} from "@/common/types/vo/ChatVO";
import {myFetch} from "@/common/utils/fetch";

export const aiReadApi = {
    // 读取文章（流式响应）
    readFile(prompt: string, fileId: number):Promise<Response>  {

        return myFetch({
            method: "POST",
            url: '/bibliophile/read/chat',
            params: {
                prompt,
                fileId,
            },
        });
    },

    // 删除所有聊天
    deleteAllChats(): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: '/bibliophile/read',
        });
    },

    // 根据文章id删除聊天
    deleteChatsByFileId(fileId: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/bibliophile/read/fileId/${fileId}`,
        });
    },

    // 根据id删除聊天
    deleteChatById(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/bibliophile/read/${id}`,
        });
    },

    // 根据文章id获取聊天记录
    getChatsByFileId(fileId: number): Promise<AxiosResponse<Result<ChatVO[]>>> {
        return myAxios({
            method: 'get',
            url: `/bibliophile/read/fileId/${fileId}`,
        });
    },

    // 根据id获取聊天记录
    getChatById(id: number): Promise<AxiosResponse<Result<ChatVO>>> {
        return myAxios({
            method: 'get',
            url: `/bibliophile/read/id/${id}`,
        });
    },

    // 下载聊天记录（md格式）
    downloadChat(id: number): Promise<AxiosResponse> {
        return myAxios({
            method: 'get',
            url: `/bibliophile/read/download/${id}`,
            responseType: 'blob' // 处理文件下载
        });
    },

    // 清空向量数据库
    deleteVectorStore(): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: '/bibliophile/read/vectorStore',
        });
    }
};