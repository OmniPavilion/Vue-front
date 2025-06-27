export interface MusicVO {
    id: number;
    title: string;
    fileName: string;
    fileSize: number;
    duration: number;
    playCount: number;
    lastPlayed?: Date;
    isFavorite: boolean;
    createdAt: Date;
    updatedAt: Date;
    singerName?: string;
    categoryName?: string;
}
