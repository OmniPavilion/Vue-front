import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { TagVO } from '@/article/types/vo/TagVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { AxiosResponse } from 'axios';

export const tagApi = {
    // 创建标签
    createTag(tag: TagVO): Promise<AxiosResponse<Result<number>>> {
        return myAxios({
            method: 'post',
            url: '/article/categories',
            data: tag,
        });
    },

    // 获取单个标签
    getTag(id: number): Promise<AxiosResponse<Result<TagVO>>> {
        return myAxios({
            method: 'get',
            url: `/article/categories/${id}`,
        });
    },

    // 更新标签
    updateTag(tag: TagVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/article/categories',
            data: tag,
        });
    },

    // 删除标签
    deleteTag(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/article/categories/${id}`,
        });
    },

    // 获取所有标签
    getAllTags(): Promise<AxiosResponse<Result<TagVO[]>>> {
        return myAxios({
            method: 'get',
            url: '/article/categories',
        });
    },

    // 分页查询标签
    getTagPage(pageDTO: PageDTO<string>): Promise<AxiosResponse<Result<PageVO<TagVO>>>> {
        return myAxios({
            method: 'post',
            url: '/article/categories/page',
            data: pageDTO,
        });
    },
};