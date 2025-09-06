// api/vocabularyApi.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { AxiosResponse } from 'axios';
import type { VocabularyQuery } from '@/english/types/dto/VocabularyQuery';
import type { Vocabulary } from '@/english/types/vo/detailWord/Vocabulary';
import type {WordText} from "@/english/types/vo/WordText";

export const vocabularyApi = {
    // 分页查询单词
    getByPage(params: PageDTO<VocabularyQuery>): Promise<AxiosResponse<Result<PageVO<Vocabulary>>>> {
        return myAxios({
            method: 'post',
            url: '/english/vocabularies/page',
            data: params
        });
    },

    // 根据id获取单词
    getById(id: number): Promise<AxiosResponse<Result<Vocabulary>>> {
        return myAxios({
            method: 'get',
            url: `/english/vocabularies/${id}`
        });
    },

    // 根据id删除单词
    deleteById(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/english/vocabularies/${id}`
        });
    },

    // 添加单词
    createWord(word: Vocabulary): Promise<AxiosResponse<Result<Vocabulary>>> {
        return myAxios({
            method: 'post',
            url: '/english/vocabularies',
            data: word
        });
    },

    // 修改单词
    updateWord(word: Vocabulary): Promise<AxiosResponse<Result<Vocabulary>>> {
        return myAxios({
            method: 'post',
            url: '/english/vocabularies/update',
            data: word
        });
    },

    // 修改单词状态
    updateWordStatus(status: number, wordId: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'post',
            url: `/english/vocabularies/status/${status}`,
            params: { wordId }
        });
    },

    // 获取单词状态数据字典
    getStatus(): Promise<AxiosResponse<Result<Map<number, string>>>> {
        return myAxios({
            method: 'get',
            url: '/english/vocabularies/status'
        });
    },

    // 获取音标数据字典
    getPartOfSpeech(): Promise<AxiosResponse<Result<Map<number, string>>>> {
        return myAxios({
            method: 'get',
            url: '/english/vocabularies/phonetic'
        });
    },

    // 获取单词选择测试题
    getTest(): Promise<AxiosResponse<Result<WordText>>> {
        return myAxios({
            method: 'get',
            url: '/english/vocabularies/test'
        });
    },

    // 批量删除单词
    deleteByIds(ids: number[]) {
        return myAxios({
            method: 'delete',
            url: '/english/vocabularies/batch',
            data: ids
        });

    }
};