export interface PageDTO<T> {
    pageNum: number;
    pageSize: number;
    order: 'ASC' | 'DESC' | 'RANDOM';
    query?: T;
}
