<script setup lang="ts">
import {ref, watch, onMounted, computed} from 'vue';
import {Category, CategoryOptions} from '@/diary/types/vo/Category';
import {useLogStore} from "@/diary/stores";
import { debounce } from 'lodash-es';
import {Weather, WeatherOptions} from "@/diary/types/vo/Weather";
import {ElMessage} from "element-plus";

const logStore = useLogStore();

const props = defineProps<{
  theme?: 'classic' | 'modern' | 'dark' | 'vintage';
}>();

// 根据主题获取Dialog样式
const dialogClass = computed(() => {
  return `note-dialog ${props.theme || 'vintage'}`;
});


// 表单数据
const formData = ref({
  pageSize: 100,
  category: undefined as Category | undefined,
  selectedMonth: '' as string | undefined,
});

// 添加日志对话框相关
const dialogVisible = ref(false);
const newLogData = ref({
  date: new Date().toISOString().slice(0, 10),
  weather: Weather.SUNNY,
});



// 防抖自动查询函数
const autoSearch = debounce(() => {
  const month = formData.value.selectedMonth;
  let query: any = {
    category: formData.value.category,
  };

  if (month) {
    const [year, monthNum] = month.split('-');
    query.startDate = `${year}-${monthNum}-01`;
    query.endDate = `${year}-${monthNum}-${new Date(parseInt(year), parseInt(monthNum), 0).getDate()}`;
  }

  logStore.pageQuery = {
    ...logStore.pageQuery,
    query,
    pageNum: 1,
    pageSize: formData.value.pageSize,
  };

  logStore.fetchLogsByPage();
}, 300);

const addLog = async () => {
  dialogVisible.value = true;
};

const confirmAddLog = async () => {
  const res = await logStore.createLog({
    id: 0,
    date: newLogData.value.date,
    weather: newLogData.value.weather,
    logs: [],
  });

  if (res.code === -1) {
    ElMessage.error(res.message as any);
    return;
  }
  ElMessage.success('添加成功' as any);

  dialogVisible.value = false;
  await logStore.fetchLogsByPage();
};

// 监听表单变化自动查询
watch(
    () => [formData.value.category, formData.value.selectedMonth],
    () => {
      autoSearch();
    },
    { deep: true }
);

// 初始化加载数据
onMounted(() => {
  logStore.fetchLogsByPage();
});
</script>

<template>
  <div class="log-search-container">
    <div class="log-search">
      <el-form :inline="true" :model="formData" class="search-form">
        <el-form-item label="分类" class="form-item">
          <el-select
              v-model="formData.category"
              style="width: 75px;"
              placeholder="分类"
              clearable
              @change="autoSearch"
              class="theme-select"
          >
            <el-option
                v-for="category in CategoryOptions"
                :key="category.value"
                :label="category.label"
                :value="category.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="数据量" class="form-item">
          <el-select
              v-model="formData.pageSize"
              style="width: 95px;"
              placeholder="数据量"
              filterable
              allow-create
              @change="(val: any) => { formData.pageSize = Number(val); autoSearch(); }"
              class="theme-select"
          >
            <el-option
                v-for="item in [10, 25, 50, 100, 250]"
                :key="item"
                :label="item"
                :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="选择月份" class="form-item">
          <el-date-picker
              v-model="formData.selectedMonth"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              @change="autoSearch"
              class="theme-date-picker"
          />
        </el-form-item>

        <el-form-item class="form-item">
          <el-button type="primary" @click="addLog" class="add-button theme-button">
            添加日志
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 添加日志对话框 -->
    <el-dialog
        :class="dialogClass"
        v-model="dialogVisible" title="添加新日志" width="500px" class="theme-dialog">
      <el-form :model="newLogData" label-width="80px">
        <el-form-item label="日期">
          <el-date-picker
              v-model="newLogData.date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="theme-date-picker"
          />
        </el-form-item>
        <el-form-item label="天气">
          <el-select v-model="newLogData.weather" placeholder="选择天气" class="theme-select">
            <el-option
                v-for="weather in WeatherOptions"
                :key="weather.value"
                :label="weather.label"
                :value="weather.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="theme-button">取消</el-button>
        <el-button type="primary" @click="confirmAddLog" class="theme-button">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.log-search-container {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 20px 0;
  position: relative;
  z-index: 1;
}

.log-search {
  width: 100%;
  max-width: 1200px;
  background-color: rgba(245, 232, 208, 0.8);
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(184, 160, 122, 0.3);
}

.search-form {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 0 20px;
}

.form-item {
  margin: 0;
}

.form-item :deep(.el-form-item__label) {
  color: #5a4a3a;
  font-weight: 500;
}

.add-button {
  margin-left: 10px;
  transition: all 0.3s ease;
}

/* 主题样式类 */
.theme-select :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(184, 160, 122, 0.5);
  box-shadow: none;
}

.theme-select :deep(.el-input__wrapper:hover) {
  border-color: #b8a07a;
}

.theme-date-picker :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(184, 160, 122, 0.5);
  box-shadow: none;
}

.theme-date-picker :deep(.el-input__wrapper:hover) {
  border-color: #b8a07a;
}

.theme-button {
  background-color: #b8a07a;
  border-color: #a38b6a;
  color: #fff;
}

.theme-button:hover {
  background-color: #a38b6a;
  border-color: #8a7557;
}

.theme-button:active {
  background-color: #8a7557;
  border-color: #715f45;
}

.theme-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.theme-dialog :deep(.el-dialog) {
  background-color: #f5e8d0;
  border: 1px solid #b8a07a;
}

.theme-dialog :deep(.el-dialog__header) {
  background-color: #b8a07a;
  margin-right: 0;
  padding: 15px 20px;
}

.theme-dialog :deep(.el-dialog__title) {
  color: white;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .log-search {
    padding: 12px;
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    padding: 0;
  }

  .form-item {
    width: 100%;
  }

  .add-button {

    margin-left: 0;
    width: 100%;
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
