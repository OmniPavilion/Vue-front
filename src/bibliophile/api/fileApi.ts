import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AiReaderFile } from '@/bibliophile/types/vo/AiReaderFile';
import type { AxiosResponse } from 'axios';

export const fileApi = {
    // 创建文件记录
    createFile(file: File): Promise<AxiosResponse<Result<number>>> {
        const formData = new FormData();
        formData.append('file', file);

        return myAxios({
            method: 'post',
            url: '/bibliophile/files',
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },

    // 获取单个文件记录
    getFile(id: number): Promise<AxiosResponse<Result<AiReaderFile>>> {
        return myAxios({
            method: 'get',
            url: `/bibliophile/files/${id}`,
        });
    },

    // 删除文件记录
    deleteFile(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/bibliophile/files/${id}`,
        });
    },

    // 获取全部文件记录
    getAllFiles(): Promise<AxiosResponse<Result<AiReaderFile[]>>> {
        return myAxios({
            method: 'get',
            url: '/bibliophile/files',
        });
    },
};

