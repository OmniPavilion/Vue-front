export interface ChatVO {
    id: number;
    conversationId: string;
    content: string;
    type: 'USER' | 'ASSISTANT' | 'SYSTEM';
    timestamp: string; // ISO格式时间字符串
}