import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';
import type {PlayArg} from "@/music/types/vo/PlayArg";

export const playApi = {
    /**
     * 获取播放参数
     * POST /api/play/arg
     */
    getPlayArg: function (): Promise<AxiosResponse<Result<PlayArg>>> {
        return myAxios({
            method: 'get',
            url: '/api/play/arg',
        });
    },

    /**
     * 社长播放参数
     * GET /api/play/arg}
     * @param playArg
     */
    setPlayArg(playArg: PlayArg): Promise<AxiosResponse<Result<null>>> {
        return myAxios({
            method: 'post',
            url: '/api/play/arg',
            data: playArg
        });
    },
};
