export interface AiReaderFile {
    id?: number | null;
    title: string;
    fileName: string;
    fileSize: number;
    aiProcessed?: number;
    createdAt?: Date | null;
    updatedAt?: Date | null;

    url: string;
}
