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
    idioms: Set<string>;
    // 同义词
    synonyms: Set<string>;
    // 反义词
    antonyms: Set<string>;
    // 上位词
    hypernyms: Set<string>;
    // 下位词
    hyponyms: Set<string>;
    // 整体词（Holonyms） - 表示该词是其中一部分的事物
    holonyms: Set<string>;
    // 部分词（Meronyms） - 表示组成该词的部分
    meronyms: Set<string>;
}