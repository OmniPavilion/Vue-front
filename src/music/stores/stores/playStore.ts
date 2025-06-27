import {defineStore} from "pinia";
import type {PlayArg} from "@/music/types/vo/PlayArg";
import { ref } from 'vue';
import {playApi} from "@/music/api/playApi";
import {musicFileApi} from "@/music/api/musicFileApi";


export const useSingerStore = defineStore('playArg', () => {
    const playArg = ref<PlayArg>();

    const getPlayArg = async function () {
        console.log("获取播放参数");
        if (playArg.value) {
            return playArg.value;
        }
        const res = await playApi.getPlayArg();
        playArg.value = res.data.data;
        console.log("获取结果", res.data);
        return res.data;
    }

    const setPlayArg = async function (playArg: PlayArg) {
        console.log("设置播放参数", playArg);
        const res = await playApi.setPlayArg(playArg);
        console.log("设置播放参数结果", res);
        return res.data;
    }

    const setRootPath = async function (path: string) {
        console.log("设置根目录", path);
        const res = await musicFileApi.updateRoot(path);
        console.log("设置根目录结果", res);
        return res.data;
    }

    const resetRootPath = async function () {
        console.log("重置根目录");
        const res = await musicFileApi.resetRoot();
        console.log("重置根目录结果", res);
        return res.data;
    }

    return {
        playArg,

        getPlayArg,
        setPlayArg,

        resetRootPath,
        setRootPath,
    }
});
