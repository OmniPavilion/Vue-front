<script setup lang="ts">
import WaveBackground from './components/WaveBackground.vue'
import SongIndex from '@/music/views/song/index.vue'
import EditCategoryButton from "@/music/components/EditCategoryButton.vue";
</script>

<template>
  <!-- 背景层 -->
  <WaveBackground/>

  <EditCategoryButton></EditCategoryButton>
  <SongIndex/>
</template>

<style scoped>

</style>
