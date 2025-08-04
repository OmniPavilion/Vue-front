import { defineStore } from 'pinia';
import { ref } from 'vue';
import { articleFileApi } from '@/article/api/articleFileApi';
import type {ArticleVO} from "@/article/types/vo/ArticleVO";

export const useArticleFileStore = defineStore('articleFile', () => {
    const fileContent = ref<string>('');
    const currentArticle = ref<ArticleVO | null>(null);
    const rootPath = ref<string>('');
    const loading = ref(false);
    const isSave = ref(true);

    // 获取文章文件内容
    const fetchArticleFile = async (articleVO : ArticleVO) => {
        console.log('当前文章:', articleVO)
        loading.value = true;
        try {
            currentArticle.value = articleVO;
            const res = await articleFileApi.getArticleFile(articleVO.id!);
            fileContent.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新文章文件内容
    const updateArticleFile = async (id: number, content: string) => {
        loading.value = true;
        try {
            const res = await articleFileApi.updateArticleFile(id, content);
            fileContent.value = content; // 更新本地状态
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 修改根文件夹路径
    const updateRoot = async (path: string) => {
        loading.value = true;
        try {
            const res = await articleFileApi.updateRootPath(path);
            rootPath.value = path;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 重置根文件夹路径
    const resetRoot = async () => {
        loading.value = true;
        try {
            const res = await articleFileApi.resetRootPath();
            // 注意：这里需要从接口获取重置后的路径，或者通过其他方式更新rootPath
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    return {
        fileContent,
        rootPath,
        loading,
        currentArticle,
        isSave,
        fetchArticleFile,
        updateArticleFile,
        updateRoot,
        resetRoot,
    };
});