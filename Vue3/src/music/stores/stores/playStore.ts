import {defineStore} from "pinia";
import { ref } from "vue";
import {musicFileApi} from "@/music/api/musicFileApi";


export const usePlayStore = defineStore('playArg', () => {
    const rootPath = ref<string>("");

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

    const getRootPath = async function () {
        console.log("获取根目录");
        const res = await musicFileApi.getRoot();
        rootPath.value = res.data.data;
        console.log("获取根目录结果", res);
        return res.data;
    }

    return {
        rootPath,

        resetRootPath,
        setRootPath,
        getRootPath,
    }
});
