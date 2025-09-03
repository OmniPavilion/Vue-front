// api/wordNetApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { AxiosResponse } from 'axios';
import type { NetWordQuery } from '@/english/types/dto/NetWordQuery';
import type { WordBasicInfo } from '@/english/types/vo/netWord/WordBasicInfo';
import type { NetWord } from '@/english/types/vo/netWord/NetWord';

export const wordNetApi = {
    // 分页查询单词
    getWordNetByPage(params: PageDTO<NetWordQuery>): Promise<AxiosResponse<Result<PageVO<WordBasicInfo>>>> {
        return myAxios({
            method: 'post',
            url: '/english/wordNet/page',
            data: params
        });
    },

    // 获取单词完整信息
    getWordNet(word: string): Promise<AxiosResponse<Result<NetWord>>> {
        return myAxios({
            method: 'get',
            url: '/english/wordNet',
            params: { word }
        });
    },

    // 获取词性id对应表
    getPos(): Promise<AxiosResponse<Result<Map<number, string>>>> {
        return myAxios({
            method: 'get',
            url: '/english/wordNet/pos'
        });
    },

    // 获取单词的统计数据
    getStatistics(): Promise<AxiosResponse<Result<Map<string, number>>>> {
        return myAxios({
            method: 'get',
            url: '/english/wordNet/statistics'
        });
    }
};