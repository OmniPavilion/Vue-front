<script setup lang="ts">
import MusicSingerChangePage from "@/music/views/main/com/MusicSingerChangePage.vue";
import Aside from "@/music/views/main/com/Aside.vue";
import MusicPlayer from "@/music/components/MusicPlayer.vue";
import FloatingDraggable from "@/music/components/FloatingDraggable.vue";
import {onMounted, onBeforeUnmount} from "vue";
import { useMusicPlayStore } from "@/music/stores";

const musicPlayStore = useMusicPlayStore();

const handleBeforeUnload = () => {
  musicPlayStore.setPlayArg()
};



onMounted(async () => {
  await musicPlayStore.getPlayArg()
  window.addEventListener('beforeunload', handleBeforeUnload);
})

onBeforeUnmount(async () => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
})
</script>

<template>
  <FloatingDraggable>
    <MusicPlayer></MusicPlayer>
  </FloatingDraggable>
  <div class="main-layout">
    <div class="aside-container">
      <Aside />
    </div>
    <div class="content-container">
      <MusicSingerChangePage />
    </div>
  </div>
</template>

<style scoped lang="scss">
.main-layout {
  display: flex;
  width: 100%;
  height: 100%;
  border-radius: 25px;
  background: linear-gradient(
          135deg,
          rgba(35, 35, 45, 0.38) 0%,
          rgba(45, 45, 60, 0.45) 100%
  );
}

.aside-container {
  width: 15%;
  min-width: 175px;
  height: 100%;
}

.content-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}

</style>
