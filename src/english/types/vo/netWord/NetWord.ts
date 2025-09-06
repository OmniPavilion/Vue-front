// NetWord.ts
import type { NetDefinition } from './NetDefinition';

export interface NetWord {
    // 单词
    word: string;
    // 词性表
    pos: string[];
    // 词义
    definitions: NetDefinition[];
    // 相关的成语或短语
    idioms: string[];
    // 同义词
    synonyms: string[];
    // 反义词
    antonyms: string[];
    // 上位词
    hypernyms: string[];
    // 下位词
    hyponyms: string[];
    // 整体词（Holonyms） - 表示该词是其中一部分的事物
    holonyms: string[];
    // 部分词（Meronyms） - 表示组成该词的部分
    meronyms: string[];
}