// api/logApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type {DailyLogVO} from "@/diary/types/vo/DailyLogVO";
import type {LogQuery} from "@/diary/types/dto/LogQuery";
import type {PageDTO} from "@/common/types/dto/PageDTO";
import type {PageVO} from "@/common/types/vo/PageVO";
import type {AxiosResponse} from "axios";

export const logApi = {
    // 创建日志
    createLog(dailyLog: DailyLogVO): Promise<AxiosResponse<Result<number>>> {
        return myAxios({
            method: 'post',
            url: '/diary/logs',
            data: dailyLog
        });
    },

    // 删除日志
    deleteLog(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/diary/logs/${id}`
        });
    },

    // 更新日志
    updateLog(dailyLog: DailyLogVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/diary/logs',
            data: dailyLog
        });
    },

    // 获取单条日志
    getLog(id: number): Promise<AxiosResponse<Result<DailyLogVO>>> {
        return myAxios({
            method: 'get',
            url: `/diary/logs/${id}`
        });
    },

    // 分页查询日志
    getLogsByPage(params: PageDTO<LogQuery>): Promise<AxiosResponse<Result<PageVO<DailyLogVO>>>> {
        return myAxios({
            method: 'post',
            url: '/diary/logs/page',
            data: params
        });
    }
};
