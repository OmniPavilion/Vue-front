import { myAxios } from '@/common/utils/axios';
import type { Result } from '@/common/types/vo/Result';
import type { AxiosResponse } from 'axios';

export const musicFileApi = {
    /**
     * 修改音乐文件根目录
     * PUT /api/musicFile/root
     * @param path 新的根目录路径
     */
    updateRoot(path: string): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/music/musicFile/root',
            params: { path }
        });
    },

    /**
     * 重置音乐文件根目录
     * PUT /api/musicFile/resetRoot
     */
    resetRoot(): Promise<AxiosResponse<Result<void>>> {
        return myAxios({
            method: 'put',
            url: '/music/musicFile/resetRoot'
        });
    },

    /**
     * 获取音乐文件根目录
     * GET /api/musicFile/root
     * @returns 音乐文件根目录
     */
    getRoot() {
        return myAxios({
            method: 'get',
            url: '/music/musicFile/root'
        });
    }
};
