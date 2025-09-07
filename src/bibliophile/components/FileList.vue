<script setup lang="ts">
import { useFileStore } from "@/bibliophile/stores/stores/fileStore";
import { onMounted, computed, ref } from "vue";
import FileCard from "@/bibliophile/components/FileCard.vue";
import { ElEmpty, ElSkeleton, ElButton, ElSelect, ElOption } from "element-plus";
import { Refresh, Plus, Sort } from "@element-plus/icons-vue";

const fileStore = useFileStore();

// 排序选项
const sortOptions = [
  { label: '按时间排序（新到旧）', value: 'date-desc' },
  { label: '按时间排序（旧到新）', value: 'date-asc' },
  { label: '按名称排序（A-Z）', value: 'name-asc' },
  { label: '按名称排序（Z-A）', value: 'name-desc' },
  { label: '按大小排序（大→小）', value: 'size-desc' },
  { label: '按大小排序（小→大）', value: 'size-asc' },
  { label: '按处理状态', value: 'status' }
];

const sortBy = ref('date-desc');

// 计算属性：文件是否为空
const isEmpty = computed(() => fileStore.files.length === 0);
// 计算属性：是否正在加载
const isLoading = computed(() => fileStore.loading);

// 计算属性：排序后的文件列表
const sortedFiles = computed(() => {
  const files = [...fileStore.files];

  switch (sortBy.value) {
    case 'date-desc':
      return files.sort((a, b) =>
          new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime()
      );

    case 'date-asc':
      return files.sort((a, b) =>
          new Date(a.createdAt || 0).getTime() - new Date(b.createdAt || 0).getTime()
      );

    case 'name-asc':
      return files.sort((a, b) => a.title.localeCompare(b.title));

    case 'name-desc':
      return files.sort((a, b) => b.title.localeCompare(a.title));

    case 'size-desc':
      return files.sort((a, b) => b.fileSize - a.fileSize);

    case 'size-asc':
      return files.sort((a, b) => a.fileSize - b.fileSize);

    case 'status':
      return files.sort((a, b) => {
        // 已处理的排在前面
        if (a.aiProcessed === 1 && b.aiProcessed !== 1) return -1;
        if (a.aiProcessed !== 1 && b.aiProcessed === 1) return 1;
        // 状态相同的按时间倒序
        return new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime();
      });

    default:
      return files;
  }
});

onMounted(() => {
  fileStore.fetchAllFiles();
});

// 刷新文件列表
const refreshFiles = () => {
  fileStore.fetchAllFiles();
};

// 处理文件上传
const handleUpload = () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.multiple = true;
  input.onchange = (event) => {
    const files = (event.target as HTMLInputElement).files;
    if (files && files.length > 0) {
      Array.from(files).forEach(file => {
        fileStore.uploadFile(file);
      });
    }
  };
  input.click();
};
</script>

<template>
  <div class="file-list-container">
    <!-- 头部操作栏 -->
    <div class="list-header">
      <h2 class="list-title">我的文件</h2>
      <div class="header-controls">
        <!-- 排序选择器 -->
        <div class="sort-control">
          <el-select
              v-model="sortBy"
              size="small"
              :prefix-icon="Sort"
              style="width: 180px"
          >
            <el-option
                v-for="option in sortOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </div>

        <div class="header-actions">
          <el-button
              :icon="Refresh"
              size="small"
              @click="refreshFiles"
              :loading="isLoading"
              title="刷新列表"
          >
            刷新
          </el-button>
          <el-button
              :icon="Plus"
              type="primary"
              size="small"
              @click="handleUpload"
              title="上传文件"
          >
            上传
          </el-button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-state">
      <div class="skeleton-flex">
        <el-skeleton
            v-for="n in 6"
            :key="n"
            class="file-skeleton"
            animated
        >
          <template #template>
            <div class="skeleton-content">
              <el-skeleton-item variant="circle" class="skeleton-icon" />
              <div class="skeleton-info">
                <el-skeleton-item variant="text" class="skeleton-title" />
                <el-skeleton-item variant="text" class="skeleton-details" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="isEmpty" class="empty-state">
      <el-empty description="暂无文件" :image-size="200">
        <p class="empty-text">上传您的第一个文件开始使用AI阅读器</p>
        <el-button
            :icon="Plus"
            type="primary"
            @click="handleUpload"
        >
          上传文件
        </el-button>
      </el-empty>
    </div>

    <!-- 文件列表 - 横向排列自动换行 -->
    <div v-else class="files-flex-container">
      <div class="files-flex-wrap">
        <FileCard
            v-for="file in sortedFiles"
            :key="file.id"
            :ai-reader-file="file"
            class="file-item"
        />
      </div>
    </div>

    <!-- 底部统计信息 -->
    <div v-if="!isEmpty && !isLoading" class="list-footer">
      <span class="file-count">共 {{ fileStore.files.length }} 个文件</span>
    </div>
  </div>
</template>

<style scoped>
.file-list-container {
  padding: 24px;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
  gap: 16px;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sort-control {
  display: flex;
  align-items: center;
}

.list-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #495057;
  margin: 0;
  white-space: nowrap;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 加载状态 */
.loading-state {
  padding: 20px 0;
}

.skeleton-flex {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.file-skeleton {
  width: 280px;
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  flex-shrink: 0;
}

.skeleton-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.skeleton-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
}

.skeleton-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-title {
  width: 70%;
  height: 16px;
}

.skeleton-details {
  width: 50%;
  height: 12px;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
}

.empty-text {
  color: #6c757d;
  margin-bottom: 20px;
  font-size: 0.95rem;
}

/* 文件横向排列布局 */
.files-flex-container {
  height: 80%;
  width: 100%;
  overflow-y: auto;
}

.files-flex-container::-webkit-scrollbar {
  display: none;
}

.files-flex-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 8px;
  margin: -4px;
}

.file-item {
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.file-item:hover {
  transform: translateY(-2px);
}

/* 底部统计 */
.list-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 16px 8px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  font-size: 0.875rem;
  color: #6c757d;
}

.file-count {
  font-weight: 500;
}

.processed-count {
  color: #28a745;
}

/* 暗色模式适配 */
:deep(.ai-reader-background.dark-mode) .list-title {
  color: #e9ecef;
}

:deep(.ai-reader-background.dark-mode) .file-skeleton {
  background: rgba(52, 58, 64, 0.8);
}

:deep(.ai-reader-background.dark-mode) .empty-text {
  color: #adb5bd;
}

:deep(.ai-reader-background.dark-mode) .list-footer {
  border-top-color: rgba(255, 255, 255, 0.1);
  color: #adb5bd;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .file-list-container {
    padding: 20px;
  }

  .files-flex-wrap {
    gap: 16px;
  }

  .file-skeleton {
    width: 260px;
  }
}

@media (max-width: 768px) {
  .file-list-container {
    padding: 16px;
  }

  .list-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .header-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .sort-control {
    justify-content: center;
  }

  .header-actions {
    justify-content: center;
  }

  .files-flex-wrap {
    gap: 12px;
    justify-content: center;
  }

  .file-skeleton {
    width: 100%;
    max-width: 300px;
  }

  .list-footer {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .files-flex-wrap {
    gap: 8px;
  }

  .header-actions {
    flex-direction: column;
  }

  .sort-control {
    :deep(.el-select) {
      width: 100% !important;
    }
  }
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 排序选择器样式优化 */
:deep(.el-select .el-input__inner) {
  font-size: 0.875rem;
}
</style>