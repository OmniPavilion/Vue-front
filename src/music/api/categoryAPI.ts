// src/api/categoryAPI.ts
import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { PageVO } from '@/common/types/vo/PageVO';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { AxiosResponse } from 'axios';
import type {CategoryVO} from "@/music/types/vo/CategoryVO";

export const categoryAPI = {
    /**
     * 分页获取分类列表
     * POST /api/categories/page
     * @param params 分页查询参数
     */
    getCategories(params: PageDTO<string>): Promise<AxiosResponse<Result<PageVO<CategoryVO>>>> {
        return myAxios({
            method: 'post',
            url: '/api/categories/page',
            data: params
        });
    },

    /**
     * 获取单个分类详情
     * GET /api/categories/{id}
     * @param id 分类ID
     */
    getCategoryById(id: number): Promise<AxiosResponse<Result<CategoryVO>>> {
        return myAxios({
            method: 'get',
            url: `/api/categories/${id}`
        });
    },

    /**
     * 创建分类
     * POST /api/categories
     * @param category 分类数据
     */
    createCategory(category: CategoryVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'post',
            url: '/api/categories',
            data: category
        });
    },

    /**
     * 更新分类
     * PUT /api/categories
     * @param category 分类数据
     */
    updateCategory(category: CategoryVO): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/api/categories',
            data: category
        });
    },

    /**
     * 删除分类
     * DELETE /api/categories/{id}
     * @param id 分类ID
     */
    deleteCategory(id: number): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'delete',
            url: `/api/categories/${id}`
        });
    }
};
