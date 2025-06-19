<template>
  <div class="not-found-container">
    <SpaceBackground />
    <ErrorContent />
    <AstronautIllustration />
  </div>
</template>

<script setup lang="ts">
import SpaceBackground from './com/SpaceBackground.vue';
import ErrorContent from './com/ErrorContent.vue';
import AstronautIllustration from './com/AstronautIllustration.vue';
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a1a, #050510);
  color: white;
  padding: 2rem;
  overflow: hidden;
  position: relative;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .not-found-container {
    flex-direction: column;
    text-align: center;
  }

  .illustration {
    margin-left: 0;
    margin-top: 2rem;
  }

  .digit {
    font-size: 5rem;
  }

  .big-star {
    display: none; /* 在小屏幕上隐藏大恒星以提升性能 */
  }
}
</style>
