import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { TagVO } from '@/article/types/vo/TagVO';
import { tagApi } from '@/article/api/tagApi';
import type { PageDTO } from '@/common/types/dto/PageDTO';

export const useTagStore = defineStore('tag', () => {
    const tags = ref<TagVO[]>([]);
    const allTags = ref<TagVO[]>([]);
    const currentTag = ref<TagVO | null>(null);
    const total = ref(0);
    const pageQuery = ref<PageDTO<string>>({
        pageNum: 1,
        pageSize: 10,
        order: 'ASC',
        query: '',
    });
    const loading = ref(false);

    // 创建标签
    const createTag = async (tag: TagVO) => {
        loading.value = true;
        try {
            const res = await tagApi.createTag(tag);
            await fetchAllTags(); // 刷新标签列表
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单个标签
    const fetchTag = async (id: number) => {
        loading.value = true;
        try {
            const res = await tagApi.getTag(id);
            currentTag.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新标签
    const updateTag = async (tag: TagVO) => {
        loading.value = true;
        try {
            const res = await tagApi.updateTag(tag);
            await fetchAllTags(); // 刷新标签列表
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除标签
    const deleteTag = async (id: number) => {
        loading.value = true;
        try {
            const res = await tagApi.deleteTag(id);
            tags.value = tags.value.filter(tag => tag.id !== id);
            allTags.value = allTags.value.filter(tag => tag.id !== id);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取所有标签（用于下拉选择等场景）
    const fetchAllTags = async () => {
        loading.value = true;
        try {
            const res = await tagApi.getAllTags();
            allTags.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 分页查询标签
    const fetchTagPage = async () => {
        loading.value = true;
        try {
            const res = await tagApi.getTagPage(pageQuery.value);
            tags.value = res.data.data.rows;
            total.value = res.data.data.total;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 根据ID获取标签名称
    const getTagNameById = (id: number): string => {
        const tag = allTags.value.find(t => t.id === id);
        return tag?.name || '';
    };

    return {
        tags,
        allTags,
        currentTag,
        loading,
        pageQuery,
        total,
        createTag,
        fetchTag,
        updateTag,
        deleteTag,
        fetchAllTags,
        fetchTagPage,
        getTagNameById,
    };
});