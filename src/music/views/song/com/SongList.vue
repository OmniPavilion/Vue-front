<script setup lang="ts">
import { useMusicStore } from "@/music/stores";
import { onMounted } from "vue";
import SongCard from "@/music/views/song/com/SongCard.vue";

const musicStore = useMusicStore();

onMounted(() => {
  musicStore.fetchMusicPage();
});
</script>

<template>
  <div class="song-list-container">
    <div class="song-list-scroller">
      <SongCard
          v-for="(song, index) in musicStore.musics"
          :key="index"
          :music="song"
          class="song-card-item"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.song-list-container {
  overflow: hidden;
  border-radius: 8px;
  background-color: rgba(50, 50, 60, 0.4);
  backdrop-filter: blur(12px);
  border: 0.5px solid rgba(120, 230, 255, 0.1);
  box-shadow:
      inset 0 0 10px rgba(120, 230, 255, 0.05),
      0 0 10px rgba(0, 0, 0, 0.2);
}

.song-list-scroller {
  height: 100%;
  overflow-y: auto;
  padding: 8px;
  scrollbar-width: thin;
  scrollbar-color: rgba(120, 230, 255, 0.3) rgba(40, 40, 50, 0.3);

}

.song-card-item {
  margin-bottom: 8px;
  transition: transform 0.2s ease;

  &:last-child {
    margin-bottom: 0;
  }

  &:hover {
    transform: translateX(2px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .song-list-container {
    border-radius: 6px;
  }

  .song-list-scroller {
    padding: 6px;
  }

  .song-card-item {
    margin-bottom: 6px;
  }
}
</style>
