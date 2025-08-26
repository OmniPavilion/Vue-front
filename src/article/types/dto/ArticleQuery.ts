export interface ArticleQuery {
    title?: string;
    tagId?: number;
    startTime?: string; // ISO格式日期字符串
    endTime?: string; // ISO格式日期字符串
}