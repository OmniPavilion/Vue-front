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
  max-height: 60%;
  overflow: hidden;
  border-radius: 8px;
}

.song-list-scroller {
  height: 100%;
  overflow-y: auto;
  padding: 8px;
  scrollbar-color: transparent transparent;
  scrollbar-width: thin;
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
