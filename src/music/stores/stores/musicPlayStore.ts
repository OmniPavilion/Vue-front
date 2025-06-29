import {defineStore} from 'pinia';
import { ref } from 'vue'
import type {MusicVO} from "@/music/types/vo/MusicVO";


export const useMusicPlayStore = defineStore('play', () => {
   const currentMusic = ref<MusicVO | null>(null);
   const isPlaying = ref<boolean>(false);

   const pauseMusic = () => {
       isPlaying.value = false;
   }

   const playMusic = (music: MusicVO) => {
       isPlaying.value = true;
       currentMusic.value = music;
   }

   return {
       currentMusic,
       isPlaying,

       pauseMusic,
       playMusic
   };
});
