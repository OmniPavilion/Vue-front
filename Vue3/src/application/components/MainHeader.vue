<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    required: true
  }
})

const titleRef = ref<HTMLElement | null>(null);

function handleMouseMove(e: MouseEvent) {
  if (!titleRef.value) return;
  const rect = titleRef.value.getBoundingClientRect();
  const mouseX = e.clientX;
  const mouseY = e.clientY;
  // 判断鼠标是否在标题区域内
  if (
    mouseX >= rect.left &&
    mouseX <= rect.right &&
    mouseY >= rect.top &&
    mouseY <= rect.bottom
  ) {
    // 计算鼠标在标题内的百分比位置
    const x = ((mouseX - rect.left) / rect.width) * 100;
    const y = ((mouseY - rect.top) / rect.height) * 100;
    titleRef.value.style.background = `
      radial-gradient(circle at ${x}% ${y}%, #ffd700 30px, transparent 80px),
      linear-gradient(135deg, #2193b0, #6dd5ed)
    `;
    titleRef.value.style.webkitBackgroundClip = 'text';
    titleRef.value.style.backgroundClip = 'text';
    titleRef.value.style.webkitTextFillColor = 'transparent';
    titleRef.value.style.transition = 'background 0.2s';
    // 去掉金色边框
    titleRef.value.style.filter = '';
  } else {
    titleRef.value.style.background = '';
    titleRef.value.style.webkitBackgroundClip = '';
    titleRef.value.style.backgroundClip = '';
    titleRef.value.style.webkitTextFillColor = '';
    titleRef.value.style.filter = '';
  }
}

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
});
onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove);
});
</script>

<template>
  <div class="header-container">
    <h1 class="title" ref="titleRef">{{ title }}</h1>
    <p class="subtitle">{{ subtitle }}</p>
  </div>
</template>

<style scoped lang="scss">
.header-container {
  margin-bottom: 2.5rem;
  text-align: center;

  .title {
    font-size: 2.2rem;
    font-weight: 700;
    height: 125px;
    line-height: 125px;
    margin-bottom: 0;
    margin-top: 0;
    color: var(--el-text-color-primary);
    background: linear-gradient(135deg, #2193b0, #6dd5ed);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    transition: background 0.5s, color 0.5s;
    cursor: pointer;
  }

  .subtitle {
    font-size: 1.1rem;
    color: var(--el-text-color-secondary);
    line-height: 1.6;
    max-width: 700px;
    margin: 0 auto;
  }
}

div {
  user-select: none;
}
</style>
