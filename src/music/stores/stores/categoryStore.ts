// src/store/categoryStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { CategoryVO } from '@/music/types/vo/CategoryVO';
import { categoryAPI } from '@/music/api/categoryAPI';
import type { PageDTO } from '@/common/types/dto/PageDTO';

export const useCategoryStore = defineStore('category', () => {
    const categories = ref<CategoryVO[]>([]);
    const total = ref(0);
    const loading = ref(false);
    const pageQuery = ref<PageDTO<string>>({
        pageNum: 1,
        pageSize: 10,
        order: 'ASC',
        query: ''
    });

    // 分页查询分类
    const fetchCategoryPage = async () => {
        console.log('分页查询分类', pageQuery.value)
        loading.value = true;
        try {
            const res = await categoryAPI.getCategories(pageQuery.value);
            categories.value = res.data.data?.rows || [];
            total.value = res.data.data?.total || 0;

            console.log('分页查询分类成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单个分类详情
    const fetchCategoryById = async (id: number) => {
        console.log('获取单个分类详情', id);
        loading.value = true;
        try {
            const res = await categoryAPI.getCategoryById(id);
            console.log('获取单个分类详情成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 创建分类
    const createCategory = async (category: CategoryVO) => {
        console.log('创建分类', category);
        loading.value = true;
        try {
            const res = await categoryAPI.createCategory(category);
            categories.value.push(category)
            console.log('创建分类结果', res);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新分类
    const updateCategory = async (category: CategoryVO) => {
        console.log('更新分类', category);
        loading.value = true;
        try {
            const res = await categoryAPI.updateCategory(category);
            categories.value = categories.value.map(item => item.id === category.id ? category : item);
            console.log('更新分类结果', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除分类
    const deleteCategory = async (id: number) => {
        console.log('删除分类', id);
        loading.value = true;
        try {
            const res = await categoryAPI.deleteCategory(id);
            categories.value = categories.value.filter(c => c.id !== id);
            console.log('删除分类结果', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    return {
        categories,
        total,
        loading,
        pageQuery,
        fetchCategoryPage,
        fetchCategoryById,
        createCategory,
        updateCategory,
        deleteCategory
    };
});
