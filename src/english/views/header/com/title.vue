<!-- title.vue -->
<script setup lang="ts">
// 可以在这里添加国际化逻辑或其他业务逻辑
</script>

<template>
  <div class="title-container">
    <h1 class="main-title">
      <span class="chinese">英韵阁</span>
      <span class="divider">|</span>
      <span class="chinese">词海拾贝</span>
    </h1>
    <h2 class="sub-title">Rhyme Pavilion · Word Treasures</h2>
  </div>
</template>

<style scoped>
* {
  user-select: none;
}

.title-container {
  text-align: left; /* 改为左对齐 */
  color: #333;
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  position: relative;
  z-index: 1;
}

.main-title {
  font-size: 2rem; /* 适当减小主标题大小 */
  font-weight: bold;
  margin-bottom: 0.3rem;
  letter-spacing: 1px; /* 减小字间距 */
  color: white;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  align-items: center;
  gap: 10px; /* 减小间距 */
  line-height: 1.2; /* 优化行高 */
}

.main-title .chinese {
  transition: all 0.3s ease;
}

.main-title .chinese:hover {
  color: #ffeb3b;
  text-shadow: 0 0 10px rgba(255, 235, 59, 0.7);
}

.divider {
  color: rgba(255, 255, 255, 0.7);
  font-weight: normal;
  opacity: 0.7;
  font-size: 1.8rem; /* 调整分隔符大小 */
}

.sub-title {
  font-size: 1rem; /* 适当减小副标题大小 */
  font-weight: 400;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 0.5px; /* 减小字间距 */
  font-family: Arial, sans-serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
  margin-top: 5px; /* 减小上边距 */
  line-height: 1.3; /* 优化行高 */
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-title {
    font-size: 1.6rem;
    flex-direction: row; /* 保持水平排列 */
    gap: 8px;
  }

  .divider {
    font-size: 1.4rem;
  }

  .sub-title {
    font-size: 0.9rem;
    display: block; /* 保持显示副标题 */
  }
}

@media (max-width: 480px) {
  .main-title {
    font-size: 1.4rem;
    flex-direction: column; /* 超小屏幕改为垂直排列 */
    gap: 2px;
    align-items: flex-start; /* 左对齐 */
  }

  .divider {
    display: none; /* 超小屏幕隐藏分隔符 */
  }

  .sub-title {
    font-size: 0.8rem;
    text-align: left; /* 左对齐 */
  }
}
</style>
