<script setup lang="ts">
import { computed, ref } from 'vue'
import {ElCard, ElIcon, ElButton, ElMessageBox, ElMessage} from 'element-plus'
import {
  Document,
  VideoPlay,
  Picture,
  Clock,
  Microphone,
  Delete
} from '@element-plus/icons-vue'
import type { AiReaderFile } from '@/bibliophile/types/vo/AiReaderFile'
import { useFileStore, useAiReadStore } from "@/bibliophile/stores";

// 定义组件属性
interface Props {
  aiReaderFile: AiReaderFile
}

const fileStore = useFileStore()
const aiReadStore = useAiReadStore()

const props = defineProps<Props>()
const isHovered = ref(false)

// 根据文件扩展名获取图标
const getFileIcon = computed(() => {
  const fileName = props.aiReaderFile.fileName.toLowerCase()

  if (fileName.endsWith('.pdf')) return Document
  if (fileName.endsWith('.doc') || fileName.endsWith('.docx')) return Document
  if (fileName.endsWith('.txt') || fileName.endsWith('.md')) return Document
  if (fileName.endsWith('.jpg') || fileName.endsWith('.jpeg') || fileName.endsWith('.png') || fileName.endsWith('.gif')) return Picture
  if (fileName.endsWith('.mp4') || fileName.endsWith('.avi') || fileName.endsWith('.mov')) return VideoPlay
  if (fileName.endsWith('.mp3') || fileName.endsWith('.wav') || fileName.endsWith('.flac')) return Microphone

  return Document // 默认图标
})

// 根据文件类型设置图标颜色
const getFileIconColor = computed(() => {
  const fileName = props.aiReaderFile.fileName.toLowerCase()

  if (fileName.endsWith('.pdf')) return '#e63946'
  if (fileName.endsWith('.doc') || fileName.endsWith('.docx')) return '#3a86ff'
  if (fileName.endsWith('.txt') || fileName.endsWith('.md')) return '#2a9d8f'
  if (fileName.endsWith('.jpg') || fileName.endsWith('.jpeg') || fileName.endsWith('.png') || fileName.endsWith('.gif')) return '#f4a261'
  if (fileName.endsWith('.mp4') || fileName.endsWith('.avi') || fileName.endsWith('.mov')) return '#e76f51'
  if (fileName.endsWith('.mp3') || fileName.endsWith('.wav') || fileName.endsWith('.flac')) return '#6d6875'

  return '#6c757d' // 默认颜色
})

// 格式化文件大小
const formatFileSize = (size: number): string => {
  if (size === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (date: Date | string): string => {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 处理删除操作
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
        `确定要删除文件"${props.aiReaderFile.title}"吗？此操作不可恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          customClass: 'delete-confirm-dialog'
        }
    )
    if (props.aiReaderFile.id) {
      const res = await fileStore.deleteFile(props.aiReaderFile.id)
      if (res.code === 1) {
        ElMessage.success('文件已删除！' as any)
      } else {
        ElMessage.error(res.message as any)
      }
    }
  } catch (error) {
    // 用户取消了删除
    console.log('删除操作已取消')
  }
}

const handleClick = () => {
  aiReadStore.currentFile = props.aiReaderFile
}
</script>

<template>
  <el-card
      class="file-card"
      :body-style="{ padding: '16px', position: 'relative' }"
      shadow="hover"
      @mouseenter="isHovered = true"
      @mouseleave="isHovered = false"
      @click="handleClick"
  >
    <!-- 删除按钮 - 悬浮时显示 -->
    <div class="delete-button-wrapper" :class="{ 'visible': isHovered }">
      <el-button
          :icon="Delete"
          size="small"
          type="danger"
          circle
          @click.stop="handleDelete"
          class="delete-btn"
          title="删除文件"
      />
    </div>

    <div class="file-content">
      <!-- 文件图标 -->
      <div class="file-icon">
        <el-icon :size="42" :color="getFileIconColor">
          <component :is="getFileIcon" />
        </el-icon>
      </div>

      <!-- 文件信息 -->
      <div class="file-info">
        <div class="file-title" :title="aiReaderFile.title">
          {{ aiReaderFile.title }}
        </div>

        <div class="file-details">
          <span class="file-size">{{ formatFileSize(aiReaderFile.fileSize) }}</span>
        </div>

        <div v-if="aiReaderFile.createdAt" class="file-meta">
          <el-icon size="12"><Clock /></el-icon>
          <span class="create-time">{{ formatDate(aiReaderFile.createdAt) }}</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.file-card {
  width: 100%;
  max-width: 280px;
  min-height: 100px;
  transition: all 0.3s ease;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.08),
      0 1px 3px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.file-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow:
      0 8px 30px rgba(0, 0, 0, 0.12),
      0 2px 8px rgba(0, 0, 0, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.95);
}

.file-card:hover .delete-btn {
  display: inline;
}

/* 删除按钮样式 */
.delete-button-wrapper {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.delete-button-wrapper.visible {
  opacity: 1;
  transform: translateY(0);
}

.delete-btn {
  display: none;

  width: 28px;
  height: 28px;
  padding: 0;

  :deep(.el-icon) {
    font-size: 14px;
  }
}

.delete-btn:hover {
  transform: scale(1.1);
}

.file-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.file-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: rgba(248, 249, 250, 0.8);
  border-radius: 10px;
  border: 1px solid rgba(233, 236, 239, 0.5);
  box-shadow: inset 0 1px 2px rgba(255, 255, 255, 0.8);
  transition: all 0.2s ease;
}

.file-card:hover .file-icon {
  transform: scale(1.05);
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-title {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 6px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  letter-spacing: -0.01em;
}

.file-details {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.file-size {
  font-size: 11px;
  color: #6c757d;
  font-weight: 500;
  letter-spacing: -0.01em;
}

.process-tag {
  font-size: 10px;
  font-weight: 500;
  padding: 0 6px;
  height: 20px;
  line-height: 20px;
  border: none;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: #adb5bd;
}

.create-time {
  font-size: 10px;
  letter-spacing: -0.01em;
}

/* 暗色模式适配 */
:deep(.ai-reader-background.dark-mode) .file-card {
  background: rgba(52, 58, 64, 0.9);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.3),
      0 1px 3px rgba(0, 0, 0, 0.2);
}

:deep(.ai-reader-background.dark-mode) .file-card:hover {
  background: rgba(52, 58, 64, 0.95);
  border-color: rgba(255, 255, 255, 0.15);
  box-shadow:
      0 8px 30px rgba(0, 0, 0, 0.4),
      0 2px 8px rgba(0, 0, 0, 0.3);
}

:deep(.ai-reader-background.dark-mode) .file-icon {
  background: rgba(73, 80, 87, 0.6);
  border-color: rgba(108, 117, 125, 0.3);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.2);
}

:deep(.ai-reader-background.dark-mode) .file-title {
  color: #e9ecef;
}

:deep(.ai-reader-background.dark-mode) .file-size {
  color: #adb5bd;
}

:deep(.ai-reader-background.dark-mode) .file-meta {
  color: #6c757d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .file-card {
    max-width: 100%;
    min-height: 90px;
    border-radius: 10px;
  }

  .file-content {
    gap: 10px;
  }

  .file-icon {
    width: 44px;
    height: 44px;
  }

  .file-title {
    font-size: 12px;
  }

  .file-size {
    font-size: 10px;
  }

  .process-tag {
    font-size: 9px;
    padding: 0 5px;
    height: 18px;
    line-height: 18px;
  }

  .file-meta {
    font-size: 10px;
  }

  .create-time {
    font-size: 9px;
  }

  /* 移动端始终显示删除按钮 */
  .delete-button-wrapper {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 动画优化 */
.file-card {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.file-icon {
  transition: all 0.2s ease;
}

/* 聚焦状态 */
.file-card:focus-within {
  outline: 2px solid #3a86ff;
  outline-offset: 2px;
}

/* 删除确认对话框样式 */
:deep(.delete-confirm-dialog) {
  .el-message-box__content {
    padding: 20px;
  }

  .el-message-box__message {
    color: #6c757d;
    line-height: 1.6;
  }
}
</style>