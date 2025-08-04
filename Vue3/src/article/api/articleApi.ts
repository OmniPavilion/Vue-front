import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { ArticleVO } from '@/article/types/vo/ArticleVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { ArticleQuery } from '@/article/types/dto/ArticleQuery';
import type { AxiosResponse } from 'axios';

export const articleApi = {
    // 创建文章
    createArticle(article: ArticleVO): Promise<AxiosResponse<Result<number>>> {
        return myAxios({
            method: 'post',
            url: '/article/articles',
            data: article,
        });
    },

    // 获取单个文章
    getArticle(id: number): Promise<AxiosResponse<Result<ArticleVO>>> {
        return myAxios({
            method: 'get',
            url: `/article/articles/${id}`,
        });
    },

    // 更新文章
    updateArticle(article: ArticleVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/article/articles',
            data: article,
        });
    },

    // 删除文章
    deleteArticle(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/article/articles/${id}`,
        });
    },

    // 分页查询文章
    getArticlePage(pageDTO: PageDTO<ArticleQuery>): Promise<AxiosResponse<Result<PageVO<ArticleVO>>>> {
        return myAxios({
            method: 'post',
            url: '/article/articles/page',
            data: pageDTO,
        });
    },
};