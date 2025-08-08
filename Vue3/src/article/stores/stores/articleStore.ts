import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { ArticleVO } from '@/article/types/vo/ArticleVO';
import { articleApi } from '@/article/api/articleApi';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { ArticleQuery } from '@/article/types/dto/ArticleQuery';
import logger from "@/common/utils/logger";

export const useArticleStore = defineStore('article', () => {
    const articles = ref<ArticleVO[]>([]);
    const currentArticle = ref<ArticleVO | null>(null);
    const total = ref(0);
    const pageQuery = ref<PageDTO<ArticleQuery>>({
        pageNum: 1,
        pageSize: 99999,
        order: 'DESC',
        query: {},
    });
    const loading = ref(false);

    // 获取单个文章
    const fetchArticle = async (id: number) => {
        logger.log('获取文章', id)
        loading.value = true;
        try {
            const res = await articleApi.getArticle(id);
            currentArticle.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 创建文章
    const createArticle = async (article: ArticleVO) => {
        logger.log('创建文章', article)
        loading.value = true;
        try {
            const res = await articleApi.createArticle(article);
            articles.value.push(article);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新文章
    const updateArticle = async (article: ArticleVO) => {
        logger.log('更新文章', article)
        loading.value = true;
        try {
            const res = await articleApi.updateArticle(article);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除文章
    const deleteArticle = async (id: number) => {
        logger.log('删除文章', id)
        loading.value = true;
        try {
            const res = await articleApi.deleteArticle(id);
            articles.value = articles.value.filter(article => article.id !== id);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 分页查询文章
    const fetchArticlePage = async () => {
        logger.log('分页查询文章', pageQuery.value)
        loading.value = true;
        try {
            const response = await articleApi.getArticlePage(pageQuery.value);
            articles.value = response.data.data.rows;
            total.value = response.data.data.total;
        } finally {
            loading.value = false;
        }
    };

    // 获取当前文章
    const getCurrentArticle = async () => {
        const res = await articleApi.getCurrentArticle();
        return res.data;
    };

    return {
        articles,
        currentArticle,
        loading,
        pageQuery,
        total,
        fetchArticle,
        createArticle,
        updateArticle,
        deleteArticle,
        fetchArticlePage,
        getCurrentArticle
    };
});