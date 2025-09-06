<!-- index.vue -->
<script setup lang="ts">
import Logo from './com/logo.vue'
import Title from './com/title.vue'
import Poster from './com/poster.vue'
import Cloud from './com/cloud.vue'
import Weather from './com/weather.vue'
</script>

<template>
  <div class="header-container">
    <Weather initial-weather="sunny"/>
    <Cloud />
    <Logo />
    <Title />
    <Poster />
  </div>
</template>

<style scoped>
.header-container {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  height: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #6db3f2 0%, #1e69de 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    gap: 15px;
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .header-container {
    gap: 10px;
    padding: 10px;
    flex-wrap: wrap;
    justify-content: space-between;
  }
}
</style>
