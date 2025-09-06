// BaseWord.ts
export interface BaseWord {
    id?: number | null;
    english: string;
    chinese: string;
    phonetic?: string | null;
}