// stores/english/vocabularyStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Vocabulary } from '@/english/types/vo/detailWord/Vocabulary';
import { vocabularyApi } from '@/english/api/vocabularyApi';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { VocabularyQuery } from '@/english/types/dto/VocabularyQuery';
import logger from "@/common/utils/logger";

export const useVocabularyStore = defineStore('vocabulary', () => {
    const vocabularies = ref<Vocabulary[]>([]);
    const currentVocabulary = ref<Vocabulary | null>(null);
    const total = ref(0);
    const pageQuery = ref<PageDTO<VocabularyQuery>>({
        pageNum: 1,
        pageSize: 10,
        order: 'DESC',
        query: {},
    });
    const loading = ref(false);

    // 分页查询单词
    const fetchVocabularyPage = async () => {
        logger.log('分页查询单词', pageQuery.value);
        loading.value = true;
        try {
            const response = await vocabularyApi.getByPage(pageQuery.value);
            vocabularies.value = response.data.data.rows;
            total.value = response.data.data.total;
        } finally {
            loading.value = false;
        }
    };

    // 根据id获取单词
    const fetchVocabularyById = async (id: number) => {
        logger.log('根据id获取单词', id);
        loading.value = true;
        try {
            const response = await vocabularyApi.getById(id);
            currentVocabulary.value = response.data.data;
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 根据id删除单词
    const deleteVocabulary = async (id: number) => {
        logger.log('根据id删除单词', id);
        loading.value = true;
        try {
            const response = await vocabularyApi.deleteById(id);
            vocabularies.value = vocabularies.value.filter(vocab => vocab.id !== id);
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 添加单词
    const createVocabulary = async (word: Vocabulary) => {
        logger.log('添加单词', word);
        loading.value = true;
        try {
            const response = await vocabularyApi.createWord(word);
            if (response.data.code === 1) {
                vocabularies.value.push(response.data.data);
            }
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 修改单词
    const updateVocabulary = async (word: Vocabulary) => {
        logger.log('修改单词', word);
        loading.value = true;
        try {
            const response = await vocabularyApi.updateWord(word);
            if (response.data.code === 1) {
                const index = vocabularies.value.findIndex(v => v.id === word.id);
                if (index !== -1) {
                    vocabularies.value[index] = response.data.data;
                }
            }
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 修改单词状态
    const updateVocabularyStatus = async (status: number, wordId: number) => {
        logger.log('修改单词状态', { status, wordId });
        loading.value = true;
        try {
            const response = await vocabularyApi.updateWordStatus(status, wordId);
            return response.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单词状态数据字典
    const fetchStatusDict = async () => {
        logger.log('获取单词状态数据字典');
        loading.value = true;
        try {
            const response = await vocabularyApi.getStatus();
            return response.data.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取音标数据字典
    const fetchPartOfSpeechDict = async () => {
        logger.log('获取音标数据字典');
        loading.value = true;
        try {
            const response = await vocabularyApi.getPartOfSpeech();
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
    const setQuery = (query: VocabularyQuery) => {
        pageQuery.value.query = query;
    };

    // 设置分页参数
    const setPagination = (pageNum: number, pageSize: number) => {
        pageQuery.value.pageNum = pageNum;
        pageQuery.value.pageSize = pageSize;
    };

    return {
        vocabularies,
        currentVocabulary,
        loading,
        pageQuery,
        total,
        fetchVocabularyPage,
        fetchVocabularyById,
        deleteVocabulary,
        createVocabulary,
        updateVocabulary,
        updateVocabularyStatus,
        fetchStatusDict,
        fetchPartOfSpeechDict,
        resetQuery,
        setQuery,
        setPagination
    };
});