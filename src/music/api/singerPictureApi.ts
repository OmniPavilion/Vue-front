// src/api/singerPictureApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';

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
    ): Promise<AxiosResponse<Result<Record<number, string>>>> {
        const formData = new FormData();
        files.forEach(file => formData.append('files', file));

        return myAxios({
            method: 'put',
            url: `/music/singers/${singerId}/pictures`,
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
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
            url: `/music/singers/${singerId}/picture`,
            params: { pictureId }
        });
    }
};
