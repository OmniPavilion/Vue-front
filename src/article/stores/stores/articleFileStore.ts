import { defineStore } from 'pinia';
import { ref } from 'vue';
import { articleFileApi } from '@/article/api/articleFileApi';
import type {ArticleVO} from "@/article/types/vo/ArticleVO";
import logger from '@/common/utils/logger'


export const useArticleFileStore = defineStore('articleFile', () => {
    const fileContent = ref<string>('');
    const currentArticle = ref<ArticleVO | null>(null);
    const rootPath = ref<string>('');
    const loading = ref(false);
    const isSave = ref(true);

    // 获取文章文件内容
    const fetchArticleFile = async (articleVO : ArticleVO) => {
        logger.log('获取当前文章文本:', articleVO)
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
        logger.debug('更新文章文件内容');
        loading.value = true;
        try {
            const res = await articleFileApi.updateArticleFile(id, content);
            fileContent.value = content; // 更新本地状态
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取根文件夹路径
    const getRoot = async () => {
        logger.debug('获取根目录');
        loading.value = true;
        try {
            const res = await articleFileApi.getRootPath();
            rootPath.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    }

    // 修改根文件夹路径
    const updateRoot = async (path: string) => {
        logger.debug('修改根文件夹路径');
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
        logger.debug('重置根文件夹路径');
        loading.value = true;
        try {
            const res = await articleFileApi.resetRootPath();
            // 注意：这里需要从接口获取重置后的路径，或者通过其他方式更新rootPath
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const downloadAll = async () => {
        logger.debug('开始备份文章文件');
        loading.value = true;
        try {
            const response = await articleFileApi.downloadAll();

            // 从响应头获取文件名
            const contentDisposition = response.headers['content-disposition'];
            let filename = 'articles.zip';
            if (contentDisposition) {
                const match = contentDisposition.match(/filename\*=UTF-8''(.+?)(;|$)/);
                filename = match ? decodeURIComponent(match[1]) : filename;
            }

            // 创建下载链接
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();

            // 清理
            window.URL.revokeObjectURL(url);
            document.body.removeChild(link);

            logger.log('备份成功');
        } catch (error) {
            logger.error('备份失败:', error);
        } finally {
            loading.value = false;
        }
    };

    // 下载文章文件
    const downloadArticleFile = () => {
        if (!currentArticle.value || !fileContent.value) {
            logger.warn('没有可下载的文章内容');
            return;
        } else {
            logger.log('下载文章文件', currentArticle.value.title);
        }

        // 获取文章元数据
        const article = currentArticle.value;
        const date = article.writtenAt ? new Date(article.writtenAt) : new Date();

        // 格式化日期并添加星期
        const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
        const weekday = weekdays[date.getDay()];
        const writtenDate = date.toLocaleDateString('zh-CN') + ` ${weekday}`;

        const weather = article.weather || "无天气记录";

        // 在内容末尾添加元数据
        const contentWithMetadata = `${fileContent.value}\n\n---\n\n` +
            `- **${writtenDate}**\n` +
            `- **${weather}**\n`;

        // 创建Blob对象
        const blob = new Blob([contentWithMetadata], { type: 'text/markdown' });
        const url = URL.createObjectURL(blob);

        // 创建下载链接
        const link = document.createElement('a');
        link.href = url;
        link.download = `${article.title}.md`;

        // 触发下载
        document.body.appendChild(link);
        link.click();

        // 清理
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
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
        downloadArticleFile,
        getRoot,
        downloadAll
    };
});