// stores/english/baseWordStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { BaseWord } from '@/english/types/vo/BaseWord';
import { wordApi } from '@/english/api/baseWordApi';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { BaseWordQuery } from '@/english/types/dto/BaseWordQuery';
import logger from "@/common/utils/logger";

export const useBaseWordStore = defineStore('baseWord', () => {
    const words = ref<BaseWord[]>([]);
    const total = ref(0);
    const pageQuery = ref<PageDTO<BaseWordQuery>>({
        pageNum: 1,
        pageSize: 10,
        order: 'DESC',
        query: {},
    });
    const loading = ref(false);

    // 分页查询单词
    const fetchWordPage = async () => {
        logger.log('分页查询单词', pageQuery.value);
        loading.value = true;
        try {
            const response = await wordApi.getByPage(pageQuery.value);
            words.value = response.data.data.rows;
            total.value = response.data.data.total;
        } finally {
            loading.value = false;
        }
    };

    // 获取单词分类数据字典
    const fetchCategory = async () => {
        logger.log('获取单词分类数据字典');
        loading.value = true;
        try {
            const response = await wordApi.getCategory();
            return response.data.data;
        } finally {
            loading.value = false;
        }
    };

    return {
        words,
        loading,
        pageQuery,
        total,
        fetchWordPage,
        fetchCategory,
    };
});