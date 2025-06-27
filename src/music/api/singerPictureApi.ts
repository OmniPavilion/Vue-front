// src/api/singerPictureApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';
import type {SingerPictureResponse} from "@/music/types/dto/SingerPictureResponse";

export const singerPictureApi = {
    /**
     * 批量上传歌手图片
     * PUT /api/singers/{singerId}/pictures
     * @param singerId 歌手ID
     * @param files 图片文件数组
     */
    uploadSingerPictures(
        singerId: number,
        files: File[]
    ): Promise<AxiosResponse<Result<void>>> {
        const formData = new FormData();
        files.forEach(file => formData.append('files', file));

        return myAxios({
            method: 'put',
            url: `/api/singers/${singerId}/pictures`,
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },

    /**
     * 获取单张歌手图片
     * GET /api/singers/{singerId}/picture
     * @param singerId 歌手ID
     * @param pictureId 图片ID（可选，不传则随机返回）
     * @returns 返回图片Blob数据
     */
    getSingerPicture(
        singerId: number,
        pictureId?: number
    ): Promise<AxiosResponse<Blob>> {
        return myAxios({
            method: 'get',
            url: `/api/singers/${singerId}/picture`,
            params: { pictureId },
            responseType: 'blob'
        });
    },

    /**
     * 获取歌手所有图片
     * GET /api/singers/{singerId}/pictures
     * @param singerId 歌手ID
     * @returns 返回图片Blob数组
     */
    getSingerPictures(singerId: number): Promise<AxiosResponse<SingerPictureResponse[]>> {
        return myAxios({
            method: 'get',
            url: `/api/singers/${singerId}/pictures`,
            responseType: 'blob'
        });
    },

    /**
     * 删除歌手图片
     * DELETE /api/singers/{singerId}/picture
     * @param singerId 歌手ID
     * @param pictureId 图片ID
     */
    deleteSingerPicture(
        singerId: number,
        pictureId: number
    ): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/api/singers/${singerId}/picture`,
            params: { pictureId }
        });
    }
};
