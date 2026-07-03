// src/music/stores/musicPlayStore.ts
import {defineStore} from 'pinia';
import {ref} from 'vue';
import type {MusicVO} from "@/music/types/vo/MusicVO";
import {audioService} from '@/music/services/audioService';
import {useMusicStore} from "@/music/stores";
import type {PlayArg} from "@/music/types/vo/PlayArg";
import {playApi} from "@/music/api/playApi";
import {musicApi} from "@/music/api/musicApi";

export const useMusicPlayStore = defineStore('play', () => {
    const currentMusic = ref<MusicVO | null>(null);
    const isPlaying = ref(false);
    const musicStore = useMusicStore();
    const isInit = ref(false);

    const playArg = ref<PlayArg>({
        currentMusicId: null,
        playMode: 'order',
        volume: 100,
        playDuration: 0
    });

    const getPlayArg = async function () {
        console.log("获取播放参数");

        const res = await playApi.getPlayArg();
        playArg.value = res.data.data;

        audioService.volume = playArg.value.volume

        if (playArg.value.currentMusicId) {
            const res = await musicStore.fetchMusicById(playArg.value.currentMusicId)
            currentMusic.value = res.data;
        }

        console.log("获取播放参数结果", res.data);
        isInit.value = true
        return res.data;
    }

    const setPlayArg = async function () {
        playArg.value = {
            currentMusicId: currentMusic.value?.id ?? 0,
            playMode: playArg.value.playMode,
            volume: audioService.volume,
            playDuration: playArg.value.playDuration + audioService._playTime
        };
        audioService._playTime = 0;

        console.log("设置播放参数", playArg.value);
        const res = await playApi.setPlayArg(playArg.value);
        console.log("设置播放参数结果", res);
        return res.data;
    }


    const playMusic = async (music: MusicVO, isPlayNew = false) => {

        if (currentMusic.value?.id === music.id && !isPlayNew) {
            await togglePlayPause();
            return;
        }

        const success = await audioService.play(music);
        if (success) {
            currentMusic.value = music;
            isPlaying.value = true;
        }
        await setPlayArg();
    };

    const pauseMusic = () => {
        audioService.pause();
        isPlaying.value = false;
    };

    const togglePlayPause = async () => {
        if (isPlaying.value) {
            pauseMusic();
        } else if (currentMusic.value) {
            isPlaying.value = await audioService.play(currentMusic.value);
        }
    };

    const changePlayMode = () => {
        playArg.value.playMode = playArg.value.playMode === 'order' ? 'random' : playArg.value.playMode === 'random' ? 'loop' : 'order';
    }

    const playNext = async () => {
        const nextMusic = await musicStore.getNextMusic(true, currentMusic.value?.id ?? null, playArg.value.playMode)
        if (nextMusic) {
            await playMusic(nextMusic, true);
        }
    }

    const playPrevious = async () => {
        const previousMusic = await musicStore.getNextMusic(false, currentMusic.value?.id ?? null, playArg.value.playMode)
        if (previousMusic) {
            await playMusic(previousMusic, true);
        }
    }

    const playRandom = async () => {
        const randomMusic = await musicStore.getNextMusic(false, null, 'random')
        if (randomMusic) {
            await playMusic(randomMusic, true);
        }
    }

    audioService.onEndedCallback = playNext

    audioService.updatePlayCountCallback = async (id: number) => {
        await musicApi.recordPlay(id)
        console.log("更新播放次数", id)
    }

    return {
        currentMusic,
        isPlaying,
        playArg,
        isInit,

        playMusic,
        pauseMusic,
        togglePlayPause,
        playNext,
        playPrevious,
        changePlayMode,
        playRandom,
        getPlayArg,
        setPlayArg,
    };
});
