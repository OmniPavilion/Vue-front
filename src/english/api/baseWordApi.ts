// api/baseWordApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { AxiosResponse } from 'axios';
import type { BaseWordQuery } from '@/english/types/dto/BaseWordQuery';
import type { BaseWord } from '@/english/types/vo/BaseWord';
import type {WordText} from "@/english/types/vo/WordText";

export const wordApi = {
    // 分页查询单词
    getByPage(params: PageDTO<BaseWordQuery>): Promise<AxiosResponse<Result<PageVO<BaseWord>>>> {
        return myAxios({
            method: 'post',
            url: '/english/words/page',
            data: params
        });
    },

    // 获取单词分类数据字典
    getCategory(): Promise<AxiosResponse<Result<Map<number, string>>>> {
        return myAxios({
            method: 'get',
            url: '/english/words/category'
        });
    },

    // 获取单词选择测试题
    getTest(): Promise<AxiosResponse<Result<WordText>>> {
        return myAxios({
            method: 'get',
            url: '/english/words/test'
        });
    }
};