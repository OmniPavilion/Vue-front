import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';
import type {PlayArg} from "@/music/types/vo/PlayArg";

export const playApi = {
    /**
     * 分页获取歌手列表
     * POST /api/singers/page
     */
    getPlayArg: function (): Promise<AxiosResponse<Result<PlayArg>>> {
        return myAxios({
            method: 'post',
            url: '/api/play/arg',
        });
    },

    /**
     * 获取单个歌手详情
     * GET /api/singers/{id}
     * @param playArg
     */
    setPlayArg(playArg: PlayArg): Promise<AxiosResponse<Result<null>>> {
        return myAxios({
            method: 'get',
            url: '/api/play/arg',
            data: playArg
        });
    },
};
