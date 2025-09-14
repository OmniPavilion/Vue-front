import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';

export const articleFileApi = {
    // 获取文章文件内容
    getArticleFile(id: number): Promise<AxiosResponse<Result<string>>> {
        return myAxios({
            method: 'get',
            url: `/article/files/file/${id}`,
        });
    },


    // 更新文章文件内容
    updateArticleFile(id: number, file: string): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: `/article/files/file/${id}`,
            data: file,
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    },

    // 备份文件
    downloadAll(): Promise<AxiosResponse<Blob>> {
        return myAxios({
            method: 'get',
            url: '/article/files/downloadAll',
            responseType: 'blob'
        });
    },
};