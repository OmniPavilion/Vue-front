// src/store/musicStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { MusicVO } from '@/music/types/vo/MusicVO';
import { musicAPI } from '@/music/api/musicAPI';
import type { PageDTO } from '@/common/types/dto/PageDTO';
import type { MusicQuery } from '@/music/types/dto/MusicQuery';

export const useMusicStore = defineStore('music', () => {
    const musics = ref<MusicVO[]>([]);
    const total = ref(0);
    const loading = ref(false);
    const pageQuery = ref<PageDTO<MusicQuery>>({
        pageNum: 1,
        pageSize: 10,
        order: 'ASC',
        query: {

        }
    });

    // 分页查询音乐
    const fetchMusicPage = async () => {
        console.log('分页查询音乐', pageQuery.value);
        loading.value = true;
        try {
            const res = await musicAPI.getMusicPage(pageQuery.value);
            musics.value = res.data.data?.rows || [];
            total.value = res.data.data?.total || 0;

            console.log('分页查询音乐成功', res.data);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单个音乐详情
    const fetchMusicById = async (id: number) => {
        console.log('获取音乐详情', id)
        loading.value = true;
        try {
            const res = await musicAPI.getMusicById(id);
            console.log('获取音乐详情成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 批量上传音乐
    const createMusics = async (files: File[], singer: string, category: string) => {
        console.log('批量上传音乐')
        files.forEach(file => console.log(file.name))
        loading.value = true;
        try {
            const res = await musicAPI.createMusics(files, singer, category);
            await fetchMusicPage(); // 刷新列表
            console.log("上传成功", res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新音乐
    const updateMusic = async (music: MusicVO) => {
        console.log("更新音乐", music)
        loading.value = true;
        try {
            const res = await musicAPI.updateMusic(music);
            musics.value.map(item => item.id === music.id ? music : item)
            console.log("更新成功", res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除音乐
    const deleteMusic = async (id: number) => {
        console.log("删除音乐", id)
        loading.value = true;
        try {
            const res = await musicAPI.deleteMusic(id);
            musics.value = musics.value.filter(m => m.id !== id);
            console.log("删除成功", res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 记录播放
    const recordPlay = async (id: number) => {
        console.log("记录播放", id)
        loading.value = true;
        try {
            const res = await musicAPI.recordPlay(id);
            console.log("记录播放成功", res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 切换收藏状态
    const toggleFavorite = async (id: number) => {
        console.log("切换收藏状态", id)
        loading.value = true;
        try {
            const res = await musicAPI.toggleFavorite(id);
            const isFavorite = res.data.data;

            const music = musics.value.find(m => m.id === id);
            if (music) {
                music.isFavorite = isFavorite;
            }

            console.log("切换收藏状态成功", isFavorite)
            return res.data;
        } catch (error) {
            console.error('切换收藏状态失败:', error);
            throw error;
        } finally {
            loading.value = false;
        }
    };

    return {
        musics,
        total,
        loading,
        pageQuery,
        fetchMusicPage,
        fetchMusicById,
        createMusics,
        updateMusic,
        deleteMusic,
        recordPlay,
        toggleFavorite
    };
});
