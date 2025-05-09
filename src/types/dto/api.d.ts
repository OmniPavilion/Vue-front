// 定义响应数据的通用结构（根据你的后端接口调整）
export interface Result<T = any> {
    code: number;
    message: string;
    data: T;
}
