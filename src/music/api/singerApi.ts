// src/api/singerApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { AxiosResponse } from 'axios';
import type {SingerVO} from "@/music/types/vo/SingerVO";

export const singerApi = {
    /**
     * 分页获取歌手列表
     * POST /api/singers/page
     * @param params 分页查询参数
     */
    getSingers: function (params: PageDTO<string>): Promise<AxiosResponse<Result<PageVO<SingerVO>>>> {
        return myAxios({
            method: 'post',
            url: '/api/singers/page',
            data: params
        });
    },

    /**
     * 获取单个歌手详情
     * GET /api/singers/{id}
     * @param id 歌手ID
     */
    getSingerById(id: number): Promise<AxiosResponse<Result<SingerVO>>> {
        return myAxios({
            method: 'get',
            url: `/api/singers/${id}`
        });
    },

    /**
     * 创建歌手
     * POST /api/singers
     * @param singer 歌手数据
     */
    createSinger(singer: SingerVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'post',
            url: '/api/singers',
            data: singer
        });
    },

    /**
     * 更新歌手信息
     * PUT /api/singers
     * @param singer 歌手数据
     */
    updateSinger(singer: SingerVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/api/singers',
            data: singer
        });
    },

    /**
     * 删除歌手
     * DELETE /api/singers/{id}
     * @param id 歌手ID
     */
    deleteSinger(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/api/singers/${id}`
        });
    }
};
