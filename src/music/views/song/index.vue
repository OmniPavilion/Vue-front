<script setup lang="ts">
import SongList from "@/music/views/song/com/SongList.vue";
import SongHead from "@/music/views/song/com/SongHead.vue";
import SongPagination from "@/music/views/song/com/SongPagination.vue";
</script>

<template>
  <div class="song-container">
    <SongHead  />
    <SongList  />
    <SongPagination  />
  </div>
</template>

<style scoped lang="scss">

.song-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
  background-color: rgba(45, 45, 55, 0.15);
  backdrop-filter: blur(18px);
  border-radius: 8px;
  border: 0.5px solid rgba(120, 230, 255, 0.15);
  box-shadow: 0 0 10px rgba(120, 230, 255, 0.1),
  inset 0 0 10px rgba(120, 230, 255, 0.1);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}
</style>
