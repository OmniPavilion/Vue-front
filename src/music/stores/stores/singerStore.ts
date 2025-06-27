// src/store/singerStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { SingerVO } from '@/music/types/vo/SingerVO';
import { singerAPI } from '@/music/api/singerAPI';
import type { PageDTO } from '@/common/types/dto/PageDTO';

export const useSingerStore = defineStore('singer', () => {
    const singers = ref<SingerVO[]>([]);
    const total = ref(0);
    const loading = ref(false);
    const pageQuery = ref<PageDTO<string>>({
        pageNum: 1,
        pageSize: 10,
        order: 'ASC',
        query: ''
    });

    // 分页查询歌手
    const fetchSingerPage = async () => {
        console.log('分页查询歌手', pageQuery.value)
        loading.value = true;
        try {
            const res = await singerAPI.getSingers(pageQuery.value);
            singers.value = res.data.data?.rows || [];
            total.value = res.data.data?.total || 0;
            console.log('分页查询歌手成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 获取单个歌手详情
    const fetchSingerById = async (id: number) => {
        console.log('获取单个歌手详情',  id);
        loading.value = true;
        try {
            const res = await singerAPI.getSingerById(id);
            console.log('获取单个歌手详情成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 创建歌手
    const createSinger = async (singer: SingerVO) => {
        console.log('创建歌手', singer);
        loading.value = true;
        try {
            const res = await singerAPI.createSinger(singer);
            singers.value.push(singer)
            console.log('创建歌手结果', res);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新歌手
    const updateSinger = async (singer: SingerVO) => {
        console.log('更新歌手', singer);
        loading.value = true;
        try {
            const res = await singerAPI.updateSinger(singer);
            singers.value = singers.value.map(item => item.id === singer.id ? singer : item);
            console.log('更新歌手成功', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 删除歌手
    const deleteSinger = async (id: number) => {
        console.log('删除歌手', id)
        loading.value = true;
        try {
            const res = await singerAPI.deleteSinger(id);
            singers.value = singers.value.filter(s => s.id !== id);
            console.log('删除歌手结果', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    return {
        singers,
        total,
        loading,
        pageQuery,
        fetchSingerPage,
        fetchSingerById,
        createSinger,
        updateSinger,
        deleteSinger
    };
});
