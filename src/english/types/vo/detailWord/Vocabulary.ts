// export interface Vocabulary {.ts
import type { Detail } from './Detail';

export interface Vocabulary {
    id?: number;
    status: number;
    english: string;
    phonetic: string;
    details?: Detail[];
}