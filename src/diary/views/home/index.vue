<script setup lang="ts">
import {ref, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import {
  ElCard,
  ElRow,
  ElCol,
  ElButton,
  ElDivider,
} from 'element-plus';
import {
  Notebook,
  Calendar,
  Document,
} from '@element-plus/icons-vue';
import Background from '@/diary/components/Background.vue';
import Bookmark from '@/diary/components/Bookmark.vue';

const router = useRouter();

// 项目信息
const projectInfo = {
  name: '记事本',
  version: '1.0.0',
  description: '一款简约而不简单的个人数字管理工具，融合日记记录、计划安排与任务管理功能，助您高效规划每一天',
  features: [
    '📝 撰写和管理日记 - 支持富文本编辑与分类管理',
    '📅 制定和跟踪计划 - 可视化日历视图与进度追踪',
    '✅ 创建和完成待办事项 - 支持优先级标记与提醒设置',
    '📊 数据统计和可视化 - 自动生成周/月报表图表',
    '🔔 提醒和通知功能 - 支持跨设备消息推送'
  ]
};

// 快速操作项
const quickActions = [
  {
    title: '新建日志',
    icon: Notebook,
    color: '#FFB7B2',
    action: () => router.push({ name: 'log' })
  },
  {
    title: '添加计划',
    icon: Calendar,
    color: '#FFDAC1',
    action: () => router.push({ name: 'plan' })
  },
  {
    title: '创建待办',
    icon: Document,
    color: '#E2F0CB',
    action: () => router.push({ name: 'note' })
  },
];

onMounted(() => {
  // 这里可以添加数据获取逻辑
});
</script>

<template>
  <div class="home-container">
    <Background theme="vintage" />
    <Bookmark />

    <div class="content">
      <!-- 项目标题和简介 -->
      <ElCard class="header-card" shadow="hover">
        <h1 class="project-title">{{ projectInfo.name }}</h1>
        <p class="project-description">{{ projectInfo.description }}</p>
        <ElDivider />
        <div class="features-list">
          <h3>主要功能:</h3>
          <ul>
            <li v-for="(feature, index) in projectInfo.features" :key="index">
              {{ feature }}
            </li>
          </ul>
        </div>
      </ElCard>

      <!-- 快速操作 -->
      <ElCard shadow="hover" class="quick-actions-card">
        <h2>快速操作</h2>
        <ElDivider />
        <ElRow :gutter="20">
          <ElCol
              v-for="(action, index) in quickActions"
              :key="index"
              :xs="24" :sm="12" :md="6"
          >
            <ElButton
                class="action-button"
                :icon="action.icon as any"
                :style="{ backgroundColor: action.color, borderColor: action.color }"
                @click="action.action"
            >
              {{ action.title }}
            </ElButton>
          </ElCol>
        </ElRow>
      </ElCard>

    </div>
  </div>
</template>

<style scoped>
.home-container {
  position: relative;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  color: #4a3a2a; /* 更深的棕色文字，提高可读性 */
}

.content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 项目标题样式 */
.project-title {
  font-size: 2.2rem;
  margin-bottom: 10px;
  color: #5c4b36; /* 深棕色标题 */
  text-align: center;
  font-weight: bold;
  font-family: 'Georgia', serif; /* 更复古的字体 */
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.project-description {
  font-size: 1.1rem;
  color: #6d5c4b; /* 稍浅的棕色 */
  text-align: center;
  margin-bottom: 20px;
  font-style: italic; /* 斜体增加复古感 */
}

/* 卡片样式 */
:deep(.el-card) {
  background-color: rgba(255, 253, 245, 0.9); /* 半透明的米色背景 */
  border: 1px solid #b8a07a; /* 与背景边缘同色 */
  border-radius: 8px; /* 圆角 */
  margin-bottom: 24px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

/* 分割线样式 */
:deep(.el-divider) {
  border-color: #d4c9a8 !important; /* 柔和的米黄色分割线 */
}

/* 功能列表样式 */
.features-list {
  padding: 0 20px;
}

.features-list h3 {
  margin-bottom: 10px;
  color: #5c4b36;
  font-family: 'Georgia', serif;
}

.features-list ul {
  padding-left: 20px;
}

.features-list li {
  margin-bottom: 8px;
  color: #6d5c4b;
}

/* 快速操作按钮样式 */
.action-button {
  width: 100%;
  height: 80px;
  margin-bottom: 10px;
  font-size: 1.1rem;
  color: #5c4b36;
  transition: all 0.3s ease;
  border-radius: 6px;
  font-family: 'Georgia', serif;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* 调整快速操作按钮颜色 */
.quick-actions-card .action-button:nth-child(1) {
  background-color: #f0d9b5 !important; /* 米黄色 */
}

.quick-actions-card .action-button:nth-child(2) {
  background-color: #e6c8a0 !important; /* 浅棕色 */
}

.quick-actions-card .action-button:nth-child(3) {
  background-color: #d9b38c !important; /* 中等棕色 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .project-title {
    font-size: 1.8rem;
  }

  .project-description {
    font-size: 1rem;
  }

  .action-button {
    height: 60px;
    font-size: 1rem;
  }
}
</style>
