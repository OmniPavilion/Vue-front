// stores/english/wordNetStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { WordBasicInfo } from '@/english/types/vo/netWord/WordBasicInfo';
import type { NetWord } from '@/english/types/vo/netWord/NetWord';
import { wordNetApi } from '@/english/api/wordNetApi';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { NetWordQuery } from '@/english/types/dto/NetWordQuery';
import logger from "@/common/utils/logger";

export const useWordNetStore = defineStore('wordNet', () => {
    const wordBasicInfos = ref<WordBasicInfo[]>([]);
    const currentNetWord = ref<NetWord | null>(null);
    const total = ref(0);
    const pageQuery = ref<PageDTO<NetWordQuery>>({
        pageNum: 1,
        pageSize: 99999,
        order: 'DESC',
        query: {},
    });
    const loading = ref(false);

    // 分页查询单词基本信息
    const fetchWordNetPage = async () => {
        logger.log('分页查询单词基本信息', pageQuery.value);
        loading.value = true;
        try {
            const response = await wordNetApi.getWordNetByPage(pageQuery.value);
            wordBasicInfos.value = response.data.data.rows;
            total.value = response.data.data.total;
        } finally {
            loading.value = false;
        }
    };

    // 获取单词完整信息
    const fetchWordNet = async (word: string) => {
        logger.log('获取单词完整信息', word);
        loading.value = true;
        try {
            const response = await wordNetApi.getWordNet(word);
            currentNetWord.value = response.data.data;
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取词性id对应表
    const fetchPosDict = async () => {
        logger.log('获取词性id对应表');
        loading.value = true;
        try {
            const response = await wordNetApi.getPos();
            return response.data.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单词的统计数据
    const fetchStatistics = async () => {
        logger.log('获取单词的统计数据');
        loading.value = true;
        try {
            const response = await wordNetApi.getStatistics();
            return response.data.data;
        } finally {
            loading.value = false;
        }
    };

    // 重置查询条件
    const resetQuery = () => {
        pageQuery.value = {
            pageNum: 1,
            pageSize: 99999,
            order: 'DESC',
            query: {},
        };
    };

    // 设置查询条件
    const setQuery = (query: NetWordQuery) => {
        pageQuery.value.query = query;
    };

    // 设置分页参数
    const setPagination = (pageNum: number, pageSize: number) => {
        pageQuery.value.pageNum = pageNum;
        pageQuery.value.pageSize = pageSize;
    };

    // 清空当前单词信息
    const clearCurrentNetWord = () => {
        currentNetWord.value = null;
    };

    return {
        wordBasicInfos,
        currentNetWord,
        loading,
        pageQuery,
        total,
        fetchWordNetPage,
        fetchWordNet,
        fetchPosDict,
        fetchStatistics,
        resetQuery,
        setQuery,
        setPagination,
        clearCurrentNetWord
    };
});