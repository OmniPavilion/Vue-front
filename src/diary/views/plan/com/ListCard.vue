<script setup lang="ts">
import { ref, onMounted } from "vue";
import PlanCard from "@/diary/views/plan/com/PlanCard.vue";
import { usePlanStore } from "@/diary/stores/stores/planStore";

const planStore = usePlanStore();
const isLoading = ref(true);

onMounted(async () => {
  try {
    await planStore.fetchPlansByPage();
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="plan-list-container scroll-container">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <div class="loading-text">加载中...</div>
    </div>

    <!-- 空状态显示 -->
    <div v-else-if="!planStore.total" class="empty-container">
      <div class="empty-content">
        <div class="empty-illustration">
          <svg xmlns="http://www.w3.org/2000/svg" width="180" height="180" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <h3 class="empty-title">暂无计划</h3>
        <p class="empty-description">点击右上角按钮创建你的第一个计划</p>
      </div>
    </div>

    <!-- 有数据时的显示 -->
    <div v-else class="plan-grid">
      <PlanCard
          v-for="(plan, index) in planStore.plans"
          :key="index"
          :plan="plan"
          class="plan-card-item"
      ></PlanCard>
    </div>
  </div>
</template>

<style scoped>
.plan-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 70vh;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: var(--el-color-primary);
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

.loading-text {
  font-size: 16px;
  color: var(--el-text-color-secondary);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 网格布局 */
.plan-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px;
}

.plan-card-item {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.plan-card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* 空状态样式 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 60vh;
  padding: 40px;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  max-width: 500px;
  width: 100%;
}

.empty-illustration {
  width: 180px;
  height: 180px;
  margin-bottom: 24px;
  opacity: 0.8;
}

.empty-illustration svg {
  width: 100%;
  height: 100%;
}

.empty-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--el-text-color-primary);
}

.empty-description {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin-bottom: 24px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .plan-list-container {
    padding: 15px;
  }

  .plan-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .empty-container {
    padding: 20px;
  }

  .empty-illustration {
    width: 120px;
    height: 120px;
    margin-bottom: 16px;
  }

  .empty-title {
    font-size: 20px;
  }

  .empty-description {
    font-size: 14px;
  }
}

/* 滚动条样式 */
.scroll-container {
  max-height: 95%;
  overflow-y: auto;
  padding-right: 12px;
}

.scroll-container::-webkit-scrollbar {
  width: 8px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 4px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.scroll-container::-webkit-scrollbar-thumb:hover {
  background-color: rgba(255, 255, 255, 0.6);
}

.scroll-container::-webkit-scrollbar-track {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

/* 主题适配 */
.notebook-background.classic .empty-illustration svg {
  color: #8c7b6b;
}

.notebook-background.classic .empty-title {
  color: #5a4a42;
}

.notebook-background.modern .empty-illustration svg {
  color: #666;
}

.notebook-background.modern .empty-title {
  color: #333;
}

.notebook-background.dark .empty-illustration svg {
  color: #b8b8b8;
}

.notebook-background.dark .empty-title {
  color: #e0e0e0;
}

.notebook-background.vintage .empty-illustration svg {
  color: #8c7b6b;
}

.notebook-background.vintage .empty-title {
  color: #5a4a42;
}
</style>
