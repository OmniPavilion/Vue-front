<script setup lang="ts">
import { useLogStore } from "@/diary/stores";
import { onMounted, computed } from "vue";
import LogCard from "@/diary/views/log/com/LogCard.vue";

const logStore = useLogStore();

onMounted(async () => {
  await logStore.fetchLogsByPage();
});

// 按年月分组日志
const groupedLogs = computed(() => {
  // 初始化分组对象，结构为: { 年份: { 月份: [日志数组] } }
  // Record<string, Record<string, any[]>> 表示外层键为字符串（年份），
  // 内层值为另一个对象，其键为字符串（月份），值为日志对象数组
  const groups: Record<string, Record<string, any[]>> = {};

  // 遍历日志列表中的每一条日志
  logStore.logList.forEach(log => {
    const date = new Date(log.date);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;

    // 如果当前年份在分组中不存在，则初始化该年份的对象
    if (!groups[year]) {
      groups[year] = {};
    }

    // 如果当前月份在该年份下不存在，则初始化该月份的数组
    if (!groups[year][month]) {
      groups[year][month] = [];
    }

    // 将当前日志添加到对应的年月分组中
    groups[year][month].push(log);
  });

  // 返回分组后的结果
  return groups;
});

// 检查是否有日志数据
const hasLogs = computed(() => {
  return Object.keys(groupedLogs.value).length > 0;
});

// 获取排序后的年份数组
const sortedYears = computed(() => {
  return Object.keys(groupedLogs.value)
      .map(Number)
      .sort((a, b) => b - a); // 降序排列
});

// 获取排序后的月份数组
const sortedMonths = (year: number) => {
  return Object.keys(groupedLogs.value[year])
      .map(Number)
      .sort((a, b) => b - a); // 降序排列
};

// 月份数字转中文
const monthToChinese = (month: number) => {
  const months = [
    '一月', '二月', '三月', '四月', '五月', '六月',
    '七月', '八月', '九月', '十月', '十一月', '十二月'
  ];
  return months[month - 1];
};
</script>

<template>
  <div class="log-page scroll-container">
    <!-- 空状态显示 -->
    <div v-if="!hasLogs" class="empty-container">
      <div class="empty-content">
        <div class="empty-illustration">
          <svg xmlns="http://www.w3.org/2000/svg" width="180" height="180" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <h3 class="empty-title">暂无日志记录</h3>
      </div>
    </div>

    <!-- 有数据时的显示 -->
    <div v-else>
      <!-- 按年份分组 -->
      <div v-for="year in sortedYears" :key="year" class="year-group">
        <h2 class="year-title">{{ year }}年</h2>

        <!-- 按月分组 -->
        <div v-for="month in sortedMonths(year)" :key="month" class="month-group">
          <h3 class="month-title">{{ monthToChinese(month) }}</h3>

          <!-- 日志卡片列表 -->
          <div class="log-cards">
            <LogCard
                v-for="(log, index) in groupedLogs[year][month]"
                :key="index"
                :daily-log="log"
                class="log-card-item"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.log-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 70vh;
}

.year-group {
  margin-bottom: 30px;
}

.year-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-bottom: 20px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--el-border-color);
}

.month-group {
  margin-bottom: 20px;
  margin-left: 20px;
}

.month-title {
  font-size: 18px;
  font-weight: 500;
  color: var(--el-text-color-regular);
  margin-bottom: 15px;
}

.log-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-left: 20px;
}

.log-card-item {
  flex: 0 0 auto;
}

/* 空状态样式 - 根据主题适配 */
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

/* 响应式调整 */
@media (max-width: 768px) {
  .log-page {
    padding: 15px;
  }

  .year-title {
    font-size: 20px;
  }

  .month-title {
    font-size: 16px;
  }

  .log-cards {
    gap: 8px;
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

}

.scroll-container {
  max-height: 95%;
  overflow-y: auto;
  padding-right: 12px;
  height: 100%;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(255, 255, 255, 0.4);
    border-radius: 4px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);

    &:hover {
      background-color: rgba(255, 255, 255, 0.6);
    }
  }

  &::-webkit-scrollbar-track {
    background-color: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
  }
}
</style>
