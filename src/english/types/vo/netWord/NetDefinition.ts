// NetDefinition.ts
export interface NetDefinition {
    // 词性
    pos: string;
    // 词性中文
    posName: string;
    // 释义
    definition: string;
    // 例句
    examples: (string | null)[];
    // 同义词例句
    synonymExamples: (string | null)[];
}