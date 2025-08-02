// src/api/musicApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { MusicVO } from '@/music/types/vo/MusicVO';
import type { MusicQuery } from '@/music/types/dto/MusicQuery';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { AxiosResponse } from 'axios';

export const musicApi = {
    /**
     * 分页查询音乐
     * POST /api/musics/page
     */
    getMusicPage(params: PageDTO<MusicQuery>): Promise<AxiosResponse<Result<PageVO<MusicVO>>>> {
        return myAxios({
            method: 'post',
            url: '/api/musics/page',
            data: params
        });
    },

    /**
     * 获取单个音乐详情
     * GET /api/musics/{id}
     */
    getMusicById(id: number): Promise<AxiosResponse<Result<MusicVO>>> {
        return myAxios({
            method: 'get',
            url: `/api/musics/${id}`
        });
    },

    /**
     * 批量上传音乐
     * POST /api/musics/batch
     */
    createMusics(files: File[], singer: string, category: string): Promise<AxiosResponse<Result<void>>> {
        const formData = new FormData();
        files.forEach(file => formData.append('files', file));
        formData.append('singer', singer);
        formData.append('category', category);

        return myAxios({
            method: 'post',
            url: '/api/musics/batch',
            data: formData,
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    /**
     * 更新音乐信息
     * PUT /api/musics
     */
    updateMusic(music: MusicVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/api/musics',
            data: music
        });
    },

    /**
     * 删除音乐
     * DELETE /api/musics/{id}
     */
    deleteMusic(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/api/musics/${id}`
        });
    },

    /**
     * 批量删除音乐
     * DELETE /api/musics
     */
    deleteMusics(ids: number[]): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: '/api/musics/batch',
            data: ids
        });
    },

    /**
     * 记录播放
     * POST /api/musics/{id}/play
     */
    recordPlay(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'post',
            url: `/api/musics/${id}/play`
        });
    },

    /**
     * 切换收藏状态
     * POST /api/musics/{id}/favorite
     */
    toggleFavorite(id: number): Promise<AxiosResponse<Result<boolean>>> {
        return myAxios({
            method: 'post',
            url: `/api/musics/${id}/favorite`
        });
    },

    /**
     * 获取下一首或上一首
     * GET /api/musics/{id}/next
     */
    getNextMusic(id: number, playMode: string, isNext: boolean, query : MusicQuery) : Promise<AxiosResponse<Result<MusicVO>>> {
        return myAxios({
            method: 'post',
            url: `/api/musics/${id}/next`,
            params: {
                playMode,
                isNext,
            },
            data: query
        });
    },

    /**
     * 查询歌曲在分页中的位置
     * GET /api/musics/{id}/position
     */
    getMusicPosition(id: number, pageDTO: PageDTO<MusicQuery>): Promise<AxiosResponse<Result<number>>> {
        return myAxios({
            method: 'post',
            url: `/api/musics/${id}/position`,
            data: pageDTO
        });
    },

    checkFile(): Promise<AxiosResponse<Result<boolean>>> {
        return myAxios({
            method: 'get',
            url: `/api/musicFile/check`,
        });
    },
};
