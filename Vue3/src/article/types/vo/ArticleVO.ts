export interface ArticleVO {
    id?: number;
    title: string;
    fileName: string;
    weather: string;
    writtenAt: string; // ISO格式日期字符串
    tagIds: number[]; // 关联分类ID列表
}