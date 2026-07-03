<script setup lang="ts">
import {ref, computed, watch} from 'vue';
import {WeatherInfo} from "@/diary/types/vo/Weather";
import {Category, CategoryInfo} from "@/diary/types/vo/Category";
import type {DailyLogVO} from "@/diary/types/vo/DailyLogVO";
import {useLogStore} from "@/diary/stores";
import {ElMessage, ElMessageBox} from "element-plus";
import {Plus, Delete, Edit, CloseBold} from '@element-plus/icons-vue'
import SubcategorySelect from "@/diary/components/SubcategorySelect.vue";

const logStore = useLogStore();

const categoryToId = (cat: Category): number => {
  const map: Record<string, number> = { Study: 1, Work: 2, Life: 3, Exercise: 4, Entertainment: 5, Social: 6 };
  return map[cat] || 1;
};

const props = defineProps<{
  dailyLog: DailyLogVO;
  class?: string;
  theme?: 'classic' | 'modern' | 'dark' | 'vintage';

}>();

// 根据主题获取Dialog样式
const dialogClass = computed(() => {
  return `note-dialog ${props.theme || 'vintage'}`;
});

const dialogVisible = ref(false);
const categoryDialogVisible = ref(false);
const selectedLogIndex = ref<number | null>(null);

// 从日期字符串中提取"天"的部分
const getDayFromDate = (dateStr: string) => {
  return new Date(dateStr).getDate();
};

// 格式化完整日期显示
const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  });
};

// 获取天气图标类名
const weatherIcon = computed(() => {
  return WeatherInfo[props.dailyLog.weather].icon;
});

// 获取天气名称
const weatherName = computed(() => {
  return WeatherInfo[props.dailyLog.weather].name;
});

// 获取当天日志数量
const logCount = computed(() => {
  return props.dailyLog.logs?.length || 0;
});

// 获取分类名称
const getCategoryName = (category: Category) => {
  return CategoryInfo[category]?.name || '未知分类';
};

// 获取分类颜色
const getCategoryColor = (category: Category) => {
  return CategoryInfo[category]?.color || '#999999';
};

// 点击卡片处理
const handleClick = () => {
  dialogVisible.value = true;
};

// 添加日志
const handleAddLog = () => {
  props.dailyLog.logs.push({
    activity: '默认日志',
    category: Category.STUDY,
    subcategory: undefined
  });
};

// 删除整个日志
const handleDeleteLog = async () => {
  try {
    await ElMessageBox.confirm(
        '确定要删除这一天的所有日志吗？此操作不可恢复。',
        '警告',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          center: true,
        }
    )

    await logStore.deleteLog(props.dailyLog.id)
    ElMessage.success('删除成功' as any)
    dialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {  // 只有当不是用户取消时才显示错误
      ElMessage.error('删除失败' as any)
      console.error(error)
    }
  }
}

watch(() => dialogVisible.value, async (newValue) => {
  if (!newValue) {
    if (editingIndex.value !== null) {
      props.dailyLog.logs[editingIndex.value].activity = editText.value
      editingIndex.value = null
    }

    // 关闭弹窗时，上传数据
    await logStore.updateLog(props.dailyLog)
  }
})

// 添加编辑状态相关变量
const editingIndex = ref<number | null>(null)
const editText = ref('')

// 添加编辑方法
const handleEditLog = (index: number) => {
  if (editingIndex.value !== null) {
    props.dailyLog.logs[editingIndex.value].activity = editText.value
  }
  if (editingIndex.value === index) {
    editingIndex.value = null
    return
  }
  editingIndex.value = index
  editText.value = props.dailyLog.logs[index].activity
}

const handleDelete = async (index: number) => {
  await ElMessageBox.confirm(
      '确定要删除这条日志吗？',
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        center: true,
      }
  )
  props.dailyLog.logs.splice(index, 1)
}

// 打开分类选择对话框
const openCategoryDialog = (index: number) => {
  selectedLogIndex.value = index;
  categoryDialogVisible.value = true;
};

// 更改分类
const changeCategory = (category: Category) => {
  if (selectedLogIndex.value !== null) {
    props.dailyLog.logs[selectedLogIndex.value].category = category;
  }
  categoryDialogVisible.value = false;
};
</script>

<template>
  <el-card
      class="log-card"
      :class="props.class"
      :body-style="{ padding: '0px' }"
      shadow="hover"
      @click="handleClick"
  >
    <div class="card-content">
      <!-- 显示日期中的"天" -->
      <div class="day-number">{{ getDayFromDate(dailyLog.date) }}</div>

      <!-- 显示天气图标 -->
      <div class="weather-icon-container">
        <i :class="weatherIcon" class="weather-icon"></i>
      </div>

      <!-- 日志数量徽章 -->
      <div v-if="logCount > 0" class="log-count-badge">
        {{ logCount }}
      </div>
    </div>
  </el-card>

  <!-- 详细信息弹窗 -->
  <div>
    <el-dialog
        :class="dialogClass"
        v-model="dialogVisible"
        :title="formatDate(dailyLog.date)"
        width="80%"
        custom-class="log-detail-dialog"
    >
      <template #header>
        <div class="dialog-header">
          <span>{{ formatDate(dailyLog.date) }}</span>
          <div class="dialog-actions">
            <el-button type="primary" size="small" @click="handleAddLog" round>
              <el-icon>
                <Plus/>
              </el-icon>
              日志
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteLog" round>
              <el-icon>
                <Delete/>
              </el-icon>
              删除
            </el-button>
          </div>
        </div>
      </template>

      <div class="log-detail-content">
        <!-- 天气信息 -->
        <div class="weather-info">
          <el-tag type="info" class="weather-tag">
            <i :class="weatherIcon" class="weather-icon-dialog"></i>
            {{ weatherName }}
          </el-tag>
        </div>

        <!-- 日志条目 -->
        <div class="log-entries">
          <div v-if="logCount === 0" class="empty-logs">
            <el-empty description="这一天没有记录日志"/>
          </div>

          <!-- 优化对齐的日志条目 -->
          <div
              v-for="(log, index) in dailyLog.logs"
              :key="index"
              class="log-entry"
          >
            <el-tag
                :color="getCategoryColor(log.category)"
                effect="dark"
                class="category-tag"
                @click.stop="openCategoryDialog(index)"
            >
              {{ getCategoryName(log.category) }}
            </el-tag>

            <SubcategorySelect
                v-model="log.subcategory"
                :category-id="categoryToId(log.category)"
            />

            <div v-if="editingIndex !== index" class="activity-text">
              {{ log.activity }}
            </div>

            <!-- 编辑模式 -->
            <el-input
                v-else
                v-model="editText"
                class="edit-input"
                @keyup.enter="handleEditLog(index)"
            />

            <div class="log-actions">
              <el-button
                  type="text"
                  size="small"
                  @click.stop="handleDelete(index)"
                  class="btn delete-btn"
              >
                <el-icon class="delete-btn">
                  <CloseBold/>
                </el-icon>
              </el-button>
              <el-button
                  type="text"
                  size="small"
                  @click.stop="handleEditLog(index)"
                  class="btn edit-btn"
              >
                <el-icon :style="{ color: editingIndex === index ? 'blue' : '#666' }">
                  <Edit/>
                </el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>

  <!-- 分类选择对话框 -->
  <div>
    <el-dialog
        :class="dialogClass"
        v-model="categoryDialogVisible"
        title="选择分类"
        width="60%"
        center
    >
      <div class="category-options">
        <el-button
            v-for="(info, category) in CategoryInfo"
            :key="category"
            :style="{ backgroundColor: info.color, color: '#fff', border: 'none' }"
            @click="changeCategory(category as Category)"
        >
          {{ info.name }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.log-actions {
  width: 100px;
}

.btn {
  float: right;
  width: 10px;
}

.delete-btn {
  color: #666;
}


.subcategory-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-size: 12px;
}

.delete-subcategory-btn {
  color: #b8a07a;
  cursor: pointer;
  flex-shrink: 0;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.15s;
}

.subcategory-option:hover .delete-subcategory-btn {
  opacity: 1;
}

.delete-subcategory-btn:hover {
  color: #b85c5c;
}

.delete-btn:hover {
  color: red;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.dialog-actions {
  display: flex;
  gap: 8px;
}

.log-card {
  width: 88px;
  height: 88px;
  margin: 8px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: var(--card-bg-color, rgba(255, 255, 255, 0.85));
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1),
  0 1px 2px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--card-border-color, rgba(0, 0, 0, 0.1));
  overflow: hidden;
  position: relative;
  backdrop-filter: blur(2px);
}

.log-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12),
  0 2px 4px rgba(0, 0, 0, 0.08);
  background: var(--card-hover-bg-color, rgba(255, 255, 255, 0.92));
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 12px;
  position: relative;
}

.day-number {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--card-text-color, #2c3e50);
  font-family: 'Georgia', serif;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}

.weather-icon-container {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--weather-icon-bg, rgba(255, 255, 255, 0.7));
  border-radius: 50%;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.weather-icon {
  font-size: 18px;
  color: var(--weather-icon-color, #6b4d3a);
  transition: all 0.3s ease;
}

.log-card:hover .weather-icon {
  transform: scale(1.15);
}

/* 日志数量徽章 */
.log-count-badge {
  position: absolute;
  top: -3px;
  right: -3px;
  width: 18px;
  height: 18px;
  background-color: var(--badge-color, #b8a07a);
  color: var(--badge-text-color, #fff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 主题适配 */
.notebook-background.classic .log-card {
  --card-bg-color: rgba(249, 245, 233, 0.9);
  --card-hover-bg-color: rgba(249, 245, 233, 0.95);
  --card-border-color: rgba(212, 201, 168, 0.3);
  --card-text-color: #5a4a42;
  --weather-icon-color: #8c7b6b;
  --weather-icon-bg: rgba(255, 255, 255, 0.6);
  --badge-color: #d4c9a8;
  --badge-text-color: #5a4a42;
}

.notebook-background.modern .log-card {
  --card-bg-color: rgba(255, 255, 255, 0.9);
  --card-hover-bg-color: rgba(255, 255, 255, 0.95);
  --card-border-color: rgba(224, 224, 224, 0.5);
  --card-text-color: #333;
  --weather-icon-color: #666;
  --weather-icon-bg: rgba(255, 255, 255, 0.7);
  --badge-color: #e0e0e0;
  --badge-text-color: #333;
}

.notebook-background.dark .log-card {
  --card-bg-color: rgba(26, 26, 26, 0.8);
  --card-hover-bg-color: rgba(26, 26, 26, 0.9);
  --card-border-color: rgba(51, 51, 51, 0.5);
  --card-text-color: #e0e0e0;
  --weather-icon-color: #b8b8b8;
  --weather-icon-bg: rgba(51, 51, 51, 0.7);
  --badge-color: #333;
  --badge-text-color: #e0e0e0;
}

.notebook-background.vintage .log-card {
  --card-bg-color: rgba(245, 232, 208, 0.9);
  --card-hover-bg-color: rgba(245, 232, 208, 0.95);
  --card-border-color: rgba(184, 160, 122, 0.3);
  --card-text-color: #5a4a42;
  --weather-icon-color: #8c7b6b;
  --weather-icon-bg: rgba(255, 255, 255, 0.5);
  --badge-color: #b8a07a;
  --badge-text-color: #fff;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .log-card {
    width: 76px;
    height: 76px;
  }

  .day-number {
    font-size: 24px;
  }

  .weather-icon-container {
    width: 28px;
    height: 28px;
  }

  .weather-icon {
    font-size: 16px;
  }

  .log-count-badge {
    width: 16px;
    height: 16px;
    font-size: 9px;
  }
}

/* 弹窗样式 */
.log-detail-content {
  padding: 16px;
}

.weather-tag {
  padding: 8px 12px;
}

.log-entries {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

/* 关键改进：日志条目对齐 */
.log-entry {
  display: flex;
  align-items: flex-start; /* 顶部对齐 */
  gap: 12px;
  line-height: 1.6;
}

.category-tag {
  min-width: 80px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px; /* 微调垂直对齐 */
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.activity-text {
  flex: 1;
  padding: 6px 0;
  word-break: break-word;
}

/* 分类选择对话框样式 */
.category-options {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  padding: 16px;
}

.category-options .el-button {
  margin: 0;
  padding: 12px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.category-options .el-button:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .log-entry {
    flex-direction: column;
    gap: 8px;
  }

  .category-tag {
    margin-top: 0;
    align-self: flex-start;
  }

  .category-options {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 主题样式 */
:deep(.note-dialog.classic) {
  background: #f9f5e9;
}

:deep(.note-dialog.classic .el-dialog__header),
:deep(.note-dialog.classic .el-dialog__footer) {
  border-color: #d4c9a8;
}

:deep(.note-dialog.modern) {
  background: #ffffff;
}

:deep(.note-dialog.modern .el-dialog__header),
:deep(.note-dialog.modern .el-dialog__footer) {
  border-color: #e0e0e0;
}

:deep(.note-dialog.dark) {
  background: #1a1a1a;
}

:deep(.note-dialog.dark .el-dialog__title),
:deep(.note-dialog.dark .value) {
  color: #f0f0f0;
}

:deep(.note-dialog.dark .label),
:deep(.note-dialog.dark .progress-label),
:deep(.note-dialog.dark .progress-percentage) {
  color: #aaa;
}

:deep(.note-dialog.dark .el-dialog__header),
:deep(.note-dialog.dark .el-dialog__footer) {
  border-color: #333333;
}

:deep(.note-dialog.dark .notes-row .value) {
  background: rgba(255, 255, 255, 0.05);
}

:deep(.note-dialog.vintage) {
  background: #f5e8d0;
}

:deep(.note-dialog.vintage .el-dialog__header),
:deep(.note-dialog.vintage .el-dialog__footer) {
  border-color: #b8a07a;
}

/* 暗色主题适配 */
.dark .note-card {
  background: rgba(30, 30, 30, 0.85);
  border-color: rgba(255, 255, 255, 0.05);
}

.dark .due-time .date,
.dark .content {
  color: #f0f0f0;
}

.dark .due-time .time,
.dark .notes {
  color: #aaa;
}

.dark .progress-indicator {
  background-color: rgba(255, 255, 255, 0.08);
}

/* 暗色主题适配 */
.dark .status-option:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.dark .status-name {
  color: #f0f0f0;
}

/* 暗色主题输入框样式 */
:deep(.note-dialog.dark .theme-input .el-input__wrapper),
:deep(.note-dialog.dark .theme-input .el-textarea__inner),
:deep(.note-dialog.dark .theme-select .el-input__wrapper),
:deep(.note-dialog.dark .theme-date-picker .el-input__wrapper),
:deep(.note-dialog.dark .theme-time-picker .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.1);
  color: #f0f0f0;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1);
}

:deep(.note-dialog.dark .theme-input .el-textarea__inner) {
  background-color: rgba(255, 255, 255, 0.1);
  color: #f0f0f0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.note-dialog.dark .theme-input .el-input__wrapper:hover),
:deep(.note-dialog.dark .theme-input .el-textarea__inner:hover),
:deep(.note-dialog.dark .theme-select .el-input__wrapper:hover),
:deep(.note-dialog.dark .theme-date-picker .el-input__wrapper:hover),
:deep(.note-dialog.dark .theme-time-picker .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.3);
}

:deep(.note-dialog.dark .theme-input .el-input__wrapper.is-focus),
:deep(.note-dialog.dark .theme-input .el-textarea__inner:focus),
:deep(.note-dialog.dark .theme-select .el-input__wrapper.is-focus),
:deep(.note-dialog.dark .theme-date-picker .el-input__wrapper.is-focus),
:deep(.note-dialog.dark .theme-time-picker .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.5);
}

:deep(.note-dialog.dark .el-input__inner::placeholder),
:deep(.note-dialog.dark .el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

/* 输入框样式 */
:deep(.theme-input .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  box-shadow: 0 0 0 1px rgba(184, 160, 122, 0.3);
  transition: all 0.3s ease;
}

:deep(.theme-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b8a07a;
}

:deep(.theme-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #b8a07a;
}

:deep(.theme-input .el-textarea__inner) {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  border: 1px solid rgba(184, 160, 122, 0.3);
  transition: all 0.3s ease;
  padding: 10px;
  color: #5a4a3a;
}

:deep(.theme-input .el-textarea__inner:hover) {
  border-color: #b8a07a;
}

:deep(.theme-input .el-textarea__inner:focus) {
  border-color: #b8a07a;
  box-shadow: 0 0 0 1px #b8a07a;
}

/* 选择器样式 */
:deep(.theme-select .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  border: 1px solid rgba(184, 160, 122, 0.5); /* 明确设置边框样式 */
  box-shadow: none; /* 移除默认阴影 */
  transition: all 0.3s ease;
}

:deep(.theme-select .el-input__wrapper:hover) {
  border-color: #b8a07a;
  box-shadow: none; /* 确保悬停时也没有阴影 */
}

:deep(.theme-select .el-input__wrapper.is-focus) {
  border-color: #b8a07a;
  box-shadow: 0 0 0 1px #b8a07a; /* 聚焦时添加轻微外发光 */
}

/* 日期选择器样式 */
:deep(.theme-date-picker .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  box-shadow: 0 0 0 1px rgba(184, 160, 122, 0.3);
  transition: all 0.3s ease;
}

:deep(.theme-date-picker .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b8a07a;
}

:deep(.theme-date-picker .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #b8a07a;
}

/* 时间选择器样式 */
:deep(.theme-time-picker .el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  box-shadow: 0 0 0 1px rgba(184, 160, 122, 0.3);
  transition: all 0.3s ease;
}

:deep(.theme-time-picker .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b8a07a;
}

:deep(.theme-time-picker .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #b8a07a;
}
</style>
