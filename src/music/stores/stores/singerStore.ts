// src/store/singerStore.ts
import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {SingerVO} from '@/music/types/vo/SingerVO';
import {singerApi} from '@/music/api/singerApi';
import type {PageDTO} from '@/common/types/dto/PageDTO';
import type {Singer} from "@/music/types/bo/SInger";
import {singerPictureApi} from "@/music/api/singerPictureApi";
import type {AxiosResponse} from "axios";
import type {SingerPictureResponse} from "@/music/types/dto/SingerPictureResponse";

export const useSingerStore = defineStore('singer', () => {
    const singers = ref<Singer[]>([]);
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
            const singerVOs = res.data.data?.rows || [];
            for (const singerVO of singerVOs) {
                const res: AxiosResponse<Blob> = await singerPictureApi.getSingerPicture(singerVO.id)
                const singerPicture = res.data
                const singer: Singer = {
                    singerVO,
                    singerPicture
                }
                singers.value.push(singer);
            }
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
            const res02 = await singerPictureApi.getSingerPicture(singerVO.id);
            const singerPicture = res02.data
            const singer = {
                singerVO,
                singerPicture
            } as Singer;
            singers.value.push(singer)
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
            singers.value = singers.value.map(item => {
                if (item.singerVO.id === singerVO.id) {
                    // 创建新对象而不是直接修改，保持响应性
                    return {
                        ...item,
                        singerVO: {...singerVO} // 创建新对象
                    };
                }
                return item;
            });
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
            const res = await singerApi.deleteSinger(id);
            singers.value = singers.value.filter(s => s.singerVO.id !== id);
            console.log('删除歌手结果', res)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const getSingerPictures = async (id: number): Promise<SingerPictureResponse[]> => {
        console.log('获取歌手的图片', id)
        loading.value = true;
        try {
            const res = await singerPictureApi.getSingerPictures(id);
            console.log('获取歌手图片结果', res)
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
            console.log('删除歌手图片结果', res.data)
            return res.data;
        } finally {
            loading.value = false;
        }
    };

    const uploadSingerPictures = async (id: number, files: File[]) => {
        console.log('上传歌手图片', id, files)
        loading.value = true;
        try {
            const res = await singerPictureApi.uploadSingerPictures(id, files);
            console.log('上传歌手图片结果', res.data)
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
        deleteSinger,
        getSingerPictures,
        deleteSingerPicture,
        uploadSingerPictures
    };
});
