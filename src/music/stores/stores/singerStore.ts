// src/store/singerStore.ts
import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {SingerVO} from '@/music/types/vo/SingerVO';
import {singerApi} from '@/music/api/singerApi';
import type {PageDTO} from '@/common/types/dto/PageDTO';
import {singerPictureApi} from "@/music/api/singerPictureApi";

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
            const res = await singerApi.getSingers(pageQuery.value);
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
        console.log('获取单个歌手详情', id);
        loading.value = true;
        try {
            const res = await singerApi.getSingerById(id);
            console.log('获取单个歌手详情成功', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 创建歌手
    const createSinger = async (singerVO: SingerVO) => {
        console.log('创建歌手', singerVO);
        loading.value = true;
        try {
            const res = await singerApi.createSinger(singerVO);
            singers.value.push(singerVO)
            console.log('创建歌手结果', res);
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    // 更新歌手
    const updateSinger = async (singerVO: SingerVO) => {
        console.log('更新歌手', singerVO);
        loading.value = true;
        try {
            const res = await singerApi.updateSinger(singerVO);
            if (res.data.code === 1) {
                singers.value = singers.value.map(singer => singer.id === singerVO.id ? singerVO : singer)
                console.log('更新歌手成功', res)
            } else {
                console.log('更新歌手失败', res)
            }
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
            const res = await singerApi.deleteSinger(id);
            singers.value = singers.value.filter(s => s.id !== id);
            console.log('删除歌手结果', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const deleteSingerPicture = async (id: number, pictureId: number) => {
        console.log('删除歌手图片', id, pictureId)
        loading.value = true;
        try {
            const res = await singerPictureApi.deleteSingerPicture(id, pictureId);
            if (res.data.code === 1) {
                const targetSinger = singers.value.find(singer => singer.id === id);
                if (targetSinger) {
                    delete targetSinger.pictureMap[pictureId];
                }
            }
            console.log('删除歌手图片结果', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const uploadSingerPictures = async (id: number, files: File[]) => {
        console.log('上传歌手图片', id, files);
        loading.value = true;
        try {
            const uploadRes = await singerPictureApi.uploadSingerPictures(id, files);
            await fetchSingerPage()
            console.log('上传歌手图片结果', uploadRes.data);
            return uploadRes.data;
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
        deleteSinger,
        deleteSingerPicture,
        uploadSingerPictures
    };
});
