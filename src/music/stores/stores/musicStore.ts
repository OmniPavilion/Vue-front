// src/store/musicStore.ts
import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {MusicVO} from '@/music/types/vo/MusicVO';
import {musicApi} from '@/music/api/musicApi';
import type {PageDTO} from '@/common/types/dto/PageDTO';
import type {MusicQuery} from '@/music/types/dto/MusicQuery';

export const useMusicStore = defineStore('music', () => {
    const musics = ref<MusicVO[]>([]);
    const total = ref(0);
    const loading = ref(false);
    const pageQuery = ref<PageDTO<MusicQuery>>({
        pageNum: 1,
        pageSize: 10,
        order: 'DESC',
        query: {}
    });

    const deleteMusicIds = ref<number[]>([]);
    const isDeleteMode = ref(false);

    // 分页查询音乐
    const fetchMusicPage = async () => {
        console.log('分页查询音乐', pageQuery.value);
        loading.value = true;
        try {
            const res = await musicApi.getMusicPage(pageQuery.value);
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
            const res = await musicApi.getMusicById(id);
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
            const res = await musicApi.createMusics(files, singer, category);
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
            const res = await musicApi.updateMusic(music);
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
            const res = await musicApi.deleteMusic(id);
            musics.value = musics.value.filter(m => m.id !== id);
            total.value = total.value - 1;
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
            const res = await musicApi.recordPlay(id);
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
            const res = await musicApi.toggleFavorite(id);
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

    const deleteMusics = async (ids: number[]) => {
        console.log("批量删除音乐", ids)
        loading.value = true;
        try {
            const res = await musicApi.deleteMusics(ids);
            if (res.data.code === 1) {
                musics.value = musics.value.filter(m => !ids.includes(m.id));
                total.value = total.value - ids.length;
            }
            console.log("批量删除成功", res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    /**
     * 根据规则获取下一首/上一首歌曲
     * @param isNext true=下一首，false=上一首
     * @param currentMusicId 当前歌曲 ID
     * @param playMode 播放模式：'order'（正序）、'random'（随机）、'loop'（循环）
     * @returns 下一首/上一首歌曲（MusicVO），如果没有则返回 null
     */
    const getNextMusic = async (
        isNext: boolean,
        currentMusicId: number | null,
        playMode: 'order' | 'random' | 'loop' = 'order'
    ): Promise<MusicVO | null> => {
        if (musics.value.length === 0) return null;

        // 随机模式
        if (playMode === 'random' || currentMusicId === null) {
            const randomIndex = Math.floor(Math.random() * total.value);
            // 如果随机到的歌曲不在当前页，则加载对应页
            if (randomIndex < (pageQuery.value.pageNum - 1) * pageQuery.value.pageSize ||
                randomIndex >= pageQuery.value.pageNum * pageQuery.value.pageSize) {
                pageQuery.value.pageNum = Math.floor(randomIndex / pageQuery.value.pageSize) + 1;
                await fetchMusicPage();
            }
            return musics.value[randomIndex % pageQuery.value.pageSize];
        }

        const currentIndex = musics.value.findIndex(m => m.id === currentMusicId);
        if (currentIndex === -1) {
            console.log('通过后端接口获取下一首音乐', currentMusicId, playMode, isNext)
            const res = await musicApi.getNextMusic(currentMusicId, playMode, isNext)
            return res.data.data;
        }

        if (playMode === 'loop') {
            return musics.value[currentIndex];
        }

        // 顺序
        let nextIndex = isNext ? currentIndex + 1 : currentIndex - 1;

        // 处理边界情况
        const lastPage = Math.ceil(total.value / pageQuery.value.pageSize);
        if (nextIndex >= musics.value.length) {
            if (pageQuery.value.pageNum === lastPage) {
                pageQuery.value.pageNum = 1;
            } else {
                pageQuery.value.pageNum++;
            }
            await fetchMusicPage();
            nextIndex = 0;
        } else if (nextIndex < 0) {
            if (pageQuery.value.pageNum === 1) {
                pageQuery.value.pageNum = lastPage;
            } else {
                pageQuery.value.pageNum--;
            }
            await fetchMusicPage();
            nextIndex = musics.value.length - 1;
        }


        return musics.value[nextIndex];
    };

    return {
        musics,
        total,
        loading,
        pageQuery,
        deleteMusicIds,
        isDeleteMode,
        fetchMusicPage,
        fetchMusicById,
        createMusics,
        updateMusic,
        deleteMusic,
        recordPlay,
        toggleFavorite,
        deleteMusics,
        getNextMusic
    };
});
