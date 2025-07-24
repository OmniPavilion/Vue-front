<script setup lang="ts">
import WaveBackground from './components/WaveBackground.vue'
import Main from '@/music/views/main/index.vue'
</script>

<template>
  <!-- 背景层 -->
  <WaveBackground/>
  <div class="home-container">
    <Main  @submit.native.prevent></Main>
  </div>
</template>

<style scoped>
.home-container {
  width: 100%;
  height: 100%;
  padding: 100px;
}

</style>
