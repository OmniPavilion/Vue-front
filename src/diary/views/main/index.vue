<script setup>
import Background from '@/diary/components/Background.vue';
import Bookmark from '@/diary/components/Bookmark.vue';
import { RouterView } from 'vue-router';
</script>

<template>
  <Background
      theme="vintage"
      :showDecorativeEdge="true"
      :showGrid="true"
  />
  <Bookmark />

  <!-- 添加路由过渡动画 -->
  <router-view v-slot="{ Component }">
    <transition name="fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style scoped>
* {
  user-select: none;
}
</style>

<!-- 添加全局过渡样式 -->
<style>
/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 可选：滑动动画 */
.slide-enter-active {
  transition: all 0.3s ease-out;
}

.slide-leave-active {
  transition: all 0.3s ease-in;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
