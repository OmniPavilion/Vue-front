import { myAxios } from '@/common/utils/axios';
import type { AxiosResponse } from 'axios';

export interface Subcategory {
    name: string;
    category: number;
    createdAt?: string;
}

export const subcategoryApi = {
    listByCategory(category: number): Promise<AxiosResponse<{ code: number; data: Subcategory[] }>> {
        return myAxios({
            method: 'get',
            url: '/diary/subcategories',
            params: { category }
        });
    },

    create(name: string, category: number): Promise<AxiosResponse<{ code: number; data: Subcategory }>> {
        return myAxios({
            method: 'post',
            url: '/diary/subcategories',
            data: { name, category }
        });
    },

    deleteSubcategory(name: string): Promise<AxiosResponse<{ code: number }>> {
        return myAxios({
            method: 'delete',
            url: `/diary/subcategories/${encodeURIComponent(name)}`
        });
    }
};
