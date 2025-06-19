<template>
  <div class="content">
    <div class="error-code">
      <span class="digit">4</span>
      <span class="digit">0</span>
      <span class="digit">4</span>
    </div>
    <h1 class="title">页面未找到</h1>
    <p class="message">您访问的页面不存在或已被移除</p>
    <button
        @click="goHome"
        @mouseenter="isHovered = true"
        @mouseleave="isHovered = false"
        class="home-button"
        :class="{ 'hovered': isHovered }"
    >
      返回首页
      <svg
          class="arrow-icon"
          :class="{ 'animate': isHovered }"
          viewBox="0 0 24 24"
          width="24"
          height="24"
      >
        <path d="M5 12H19M19 12L12 5M19 12L12 19" />
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const isHovered = ref(false);

const goHome = () => {
  window.location.href = "/";
};
</script>

<style scoped>
* {
  user-select: none;
}

.content {
  max-width: 500px;
  z-index: 2;
  text-align: center;
}

.error-code {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.digit {
  font-size: 8rem;
  font-weight: 800;
  margin: 0 0.5rem;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  animation: float 3s ease-in-out infinite;
}

.digit:nth-child(1) { animation-delay: 0s; }
.digit:nth-child(2) { animation-delay: 0.2s; }
.digit:nth-child(3) { animation-delay: 0.4s; }

.title {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #e94560;
}

.message {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.8;
}

.home-button {
  display: inline-flex;
  align-items: center;
  padding: 0.8rem 1.5rem;
  background: #e94560;
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.home-button:hover {
  background: #ff6b81;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(233, 69, 96, 0.3);
}

.arrow-icon {
  margin-left: 0.5rem;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  fill: none;
  transition: transform 0.3s ease;
}

.animate {
  transform: translateX(5px);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}
</style>
