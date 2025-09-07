import { defineStore } from 'pinia';
import { ref } from 'vue';
import { fileApi } from '@/bibliophile/api/fileApi';
import type { AiReaderFile } from '@/bibliophile/types/vo/AiReaderFile';
import logger from '@/common/utils/logger';

export const useFileStore = defineStore('fileStore', () => {
    const files = ref<AiReaderFile[]>([]);
    const currentFile = ref<AiReaderFile | null>(null);
    const loading = ref(false);
    const uploadProgress = ref(0);

    const aiReaderTheme = ref<'light' | 'dark'>('dark');

    // 获取所有文件
    const fetchAllFiles = async () => {
        logger.debug('获取所有文件记录');
        loading.value = true;
        try {
            const res = await fileApi.getAllFiles();
            files.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单个文件
    const fetchFile = async (id: number) => {
        logger.debug(`获取文件记录: ${id}`);
        loading.value = true;
        try {
            const res = await fileApi.getFile(id);
            currentFile.value = res.data.data;
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 上传文件
    const uploadFile = async (file: File) => {
        logger.debug(`上传文件: ${file.name}`);
        loading.value = true;
        uploadProgress.value = 0;

        try {
            // 如果需要上传进度，可以在这里添加进度监听
            const res = await fileApi.createFile(file);

            // 上传成功后刷新文件列表
            await fetchAllFiles();

            uploadProgress.value = 100;
            return res.data;
        } catch (error) {
            uploadProgress.value = 0;
            throw error;
        } finally {
            loading.value = false;
        }
    };

    // 删除文件
    const deleteFile = async (id: number) => {
        logger.debug(`删除文件记录: ${id}`);
        try {
            const res = await fileApi.deleteFile(id);

            // 删除成功后更新文件列表
            files.value = files.value.filter(file => file.id !== id);

            // 如果删除的是当前文件，清空currentFile
            if (currentFile.value?.id === id) {
                currentFile.value = null;
            }

            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 清空文件列表
    const clearFiles = () => {
        files.value = [];
        currentFile.value = null;
    };

    // 下载文件
    const downloadFile = async (file: AiReaderFile) => {
        logger.debug(`下载文件: ${file.fileName}`);
        try {
            // 创建下载链接
            const url = file.url;
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', file.fileName);
            link.setAttribute('target', '_blank');
            document.body.appendChild(link);
            link.click();

            // 清理
            document.body.removeChild(link);

            logger.log('文件下载成功');
        } catch (error) {
            logger.error('文件下载失败:', error);
            throw error;
        }
    };

    // 格式化文件大小
    const formatFileSize = (size: number): string => {
        if (size === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(size) / Math.log(k));
        return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    return {
        files,
        currentFile,
        loading,
        uploadProgress,
        aiReaderTheme,
        fetchAllFiles,
        fetchFile,
        uploadFile,
        deleteFile,
        clearFiles,
        downloadFile,
        formatFileSize
    };
});