<script setup lang="ts">
import SingerList from "@/music/views/singer/com/SingerList.vue";
import SingerSearch from "@/music/views/singer/com/SingerSearch.vue";
</script>

<template>
  <div class="singer-page-container">
    <!-- 顶部搜索和操作区域 -->
    <div class="singer-header">
      <SingerSearch />
    </div>

    <!-- 主要内容区域 -->
    <div class="singer-main">
      <SingerList />
    </div>
  </div>
</template>

<style scoped>
.singer-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(
      135deg,
      rgba(40, 40, 50, 0.95) 0%,
      rgba(50, 50, 70, 0.9) 100%
  );
  backdrop-filter: blur(8px);
}

.singer-header {
  padding: 20px;
  background-color: rgba(45, 45, 55, 0.7);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(120, 230, 255, 0.1);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.singer-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.singer-footer {
  padding: 16px 20px;
  background-color: rgba(45, 45, 55, 0.7);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(120, 230, 255, 0.1);
  color: rgba(180, 200, 220, 0.8);
  font-size: 14px;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.divider {
  opacity: 0.5;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .singer-header,
  .singer-main,
  .singer-footer {
    padding: 12px;
  }

  .footer-content {
    flex-direction: column;
    gap: 4px;
  }

  .divider {
    display: none;
  }
}

/* 全局滚动条样式 */
:deep(::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(::-webkit-scrollbar-track) {
  background: rgba(50, 50, 60, 0.3);
  border-radius: 4px;
}

:deep(::-webkit-scrollbar-thumb) {
  background: rgba(120, 230, 255, 0.3);
  border-radius: 4px;
}

:deep(::-webkit-scrollbar-thumb:hover) {
  background: rgba(120, 230, 255, 0.5);
}
</style>
