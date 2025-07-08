// src/music/services/audioService.ts
import type {MusicVO} from "@/music/types/vo/MusicVO";
import {ref} from 'vue'

class AudioService {
    private audio = new Audio();
    private currentMusic: MusicVO | null = null;
    private _volume = 0;
    private _lastPlayTime = 0;
    private _duration = ref(0);
    private _currentTime = ref(0);
    private _progress = ref(0);
    public onEndedCallback: (() => void) = () => {
        console.log("默认实现回调");
    };

    private isPending = false; // 新增等待状态

    // 播放阈值
    private _playThreshold = 0.80;

    // 当前歌曲播放时长
    private _currentPlayTime = 0;

    private _beginTime = 0;
    public _playTime = 0

    // 更新歌曲播放次数的回调
    public updatePlayCountCallback: (id: number) => void = () => {
        console.log("更新歌曲播放次数回调的默认实现......");
    }

    constructor() {
        this.audio.preload = "auto";
        this.setupEventListeners();
    }

    private setupEventListeners() {


        this.audio.addEventListener('ended', () => {
            this._currentTime.value = 0;
            this._lastPlayTime = 0
            this.onEndedCallback(); // 触发外部传入的回调
        });

        this.audio.addEventListener('timeupdate', () => {
            this._currentTime.value = this.audio.currentTime;
            this.progress = (this.audio.currentTime / this.audio.duration) * 100;
        });
    }

    async play(music: MusicVO) {
        if (this.isPending) return false; // 如果正在处理，直接返回
        this.isPending = true;

        // 计算播放时间
        if (this._beginTime !== 0) {
            this._playTime += Math.floor(Date.now() / 1000) - this._beginTime
        }


        if (this.currentMusic) {
            this._currentPlayTime += Math.floor(Date.now() / 1000) - this._beginTime

            if (this._currentPlayTime > this._duration.value * this._playThreshold) {
                this.updatePlayCountCallback(this.currentMusic.id)
                this._currentPlayTime = 0
            }

        }

        this._beginTime = Math.floor(Date.now() / 1000)


        this.audio.src = music.url;

        if (this.currentMusic?.id === music.id && this._lastPlayTime > 0) {
            this.audio.currentTime = this._lastPlayTime;
        } else {
            this._currentTime.value = 0;
            this.audio.currentTime = 0;
        }

        this.currentMusic = music;


        try {
            await this.audio.play();
            this._duration.value = this.audio.duration;
            return true;
        } catch (error) {
            console.error("播放失败:", error);
            return false;
        } finally {
            this.isPending = false;
        }
    }

    pause() {
        if (this.isPending) return;
        this.isPending = true;
        this._lastPlayTime = this.audio.currentTime;
        this.audio.pause();

        this.isPending = false;
    }

    get currentTime() {
        return this._currentTime.value;
    }

    set currentTime(time: number) {
        this.audio.currentTime = time;
    }


    get duration() {
        return this._duration.value;
    }

    get volume() {
        return this._volume;
    }

    set volume(value: number) {
        this._volume = Math.max(0, Math.min(1, value));
        this.audio.volume = this._volume;
    }

    get progress() {
        return this._progress.value;
    }

    set progress(value: number) {
        this._progress.value = Math.max(0, Math.min(100, value));
    }
}

export const audioService = new AudioService();
