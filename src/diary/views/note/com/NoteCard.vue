<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {ElDialog, ElButton, ElMessageBox, ElMessage} from 'element-plus';
import type {NoteVO} from '@/diary/types/vo/NoteVO';
import {Status, StatusInfo} from '@/diary/types/vo/Status';
import {useNoteStore} from "@/diary/stores";

const props = defineProps<{
  note: NoteVO;
  maxNoteLength?: number;
  theme?: 'classic' | 'modern' | 'dark' | 'vintage';
}>();

const dialogVisible = ref(false);
const noteStore = useNoteStore();

// 默认值
const {maxNoteLength = 30} = props;

// 安全获取状态信息
const getStatusInfo = (status: string) => {
  const validStatus = Object.values(Status).includes(status as Status)
      ? status as Status
      : Status.PENDING;
  return StatusInfo[validStatus] || StatusInfo[Status.PENDING];
};

// 计算时间进度百分比
const progressPercentage = computed(() => {
  const dateTimeStr = `${props.note.dueDate} ${props.note.dueTime}`;

  const now = new Date().getTime();
  const dueDate = new Date(dateTimeStr).getTime();
  const startDate = dueDate - (7 * 24 * 60 * 60 * 1000);

  if (now >= dueDate) return 100;
  if (now <= startDate) return 0;
  return Math.round(((now - startDate) / (dueDate - startDate)) * 100);
});

// 格式化日期显示
const formattedDate = computed(() => {
  const date = new Date(props.note.dueDate);
  return `${date.getMonth() + 1}/${date.getDate()}`;
});

// 计算剩余时间
const timeLeft = computed(() => {
  const dateTimeStr = `${props.note.dueDate} ${props.note.dueTime}`;

  const now = new Date();
  const dueDate = new Date(dateTimeStr);
  const timeDiff = dueDate.getTime() - now.getTime();

  if (timeDiff <= 0) {
    return '已过期';
  }

  const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));

  if (days > 0) {
    return `${days}天${hours}小时`;
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟`;
  } else {
    return `${minutes}分钟`;
  }
});

// 截断过长的备注
const truncatedNotes = computed(() => {
  if (!props.note.notes) return '';
  return props.note.notes.length > maxNoteLength
      ? props.note.notes.substring(0, maxNoteLength) + '...'
      : props.note.notes;
});

// 状态相关计算属性
const statusInfo = computed(() => getStatusInfo(props.note.status));
const statusColor = computed(() => statusInfo.value.color);
const statusIcon = computed(() => statusInfo.value.icon);
const statusName = computed(() => statusInfo.value.name);

// 根据主题获取Dialog样式
const dialogClass = computed(() => {
  return `note-dialog ${props.theme || 'vintage'}`;
});

// 删除待办事项
const handleDeleteNote = async () => {
  try {
    await ElMessageBox.confirm(
        '确定要删除这个待办事项吗？此操作不可恢复。',
        '警告',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          center: true,
        }
    );

    if (props.note.id) {
      await noteStore.deleteNote(props.note.id);
      ElMessage.success('删除成功' as any);
      dialogVisible.value = false;
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败' as any);
      console.error(error);
    }
  }
};

const statusSelectVisible = ref(false);
// 打开状态选择框
const openStatusSelect = () => {
  if (props.note.status === Status.EXPIRED) {
    ElMessage.warning('已过期的笔记不能修改状态' as any);
    return;
  }
  statusSelectVisible.value = true;
};
// 更新状态
const updateStatus = async (newStatus: Status) => {
  try {
    if (newStatus === Status.EXPIRED) {
      ElMessage.warning('请选择一个有效的状态' as any);
      return;
    }

    props.note.status = newStatus;
    const updatedNote = {...props.note, status: newStatus};
    await noteStore.updateNote(updatedNote);
    ElMessage.success('状态更新成功' as any);
  } catch (error) {
    ElMessage.error('状态更新失败' as any);
    console.error(error);
  } finally {
    statusSelectVisible.value = false;
  }
};

onMounted(async () => {
  const dateTimeStr = `${props.note.dueDate} ${props.note.dueTime}`;
  const dateObj = new Date(dateTimeStr);  // 例如 "2023-05-15 14:30:00"
  // 检查是否超过截至时间
  if (props.note.dueDate && new Date(dateObj).getTime() < Date.now() &&
      props.note.status !== Status.EXPIRED && props.note.status !== Status.COMPLETED) {
    props.note.status = Status.EXPIRED;
    await noteStore.updateNote(props.note);
    await noteStore.fetchNotePage()
  }
})

</script>

<template>
  <div class="note-card" @click="dialogVisible = true" >
    <!-- 时间进度条 -->
    <div class="progress-indicator">
      <div
          class="progress-bar"
          :style="{
            transform: `scaleY(${progressPercentage / 100})`,
            background: statusColor
          }"
      ></div>
    </div>

    <!-- 截止时间 -->
    <div class="due-time">
      <div class="date">{{ formattedDate }}</div>
      <div class="time">{{ note.dueTime }}</div>
    </div>

    <!-- 主要内容 -->
    <div class="content" :title="note.content">
      {{ note.content }}
    </div>

    <!-- 备注内容 -->
    <div v-if="note.notes" class="notes" :title="note.notes">
      {{ truncatedNotes }}
    </div>

    <!-- 状态标签 -->
    <div
        class="status-tag"
        :style="{
        backgroundColor: `${statusColor}15`,
        color: statusColor,
        border: `1px solid ${statusColor}30`
      }"
        @click.stop="openStatusSelect"
    >
      <i :class="statusIcon"></i>
      <span>{{ statusName }}</span>
    </div>

  </div>

  <!-- 详细信息对话框 -->
  <div>
    <ElDialog
        v-model="dialogVisible"
        :title="note.content"
        :class="dialogClass"
        :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <div class="info-row">
          <span class="label">截止时间：</span>
          <span class="value">{{ formattedDate }} {{ note.dueTime }}</span>
        </div>

        <div class="info-row">
          <span class="label">剩余时间：</span>
          <span class="value">{{ timeLeft }}</span>
        </div>

        <div class="info-row">
          <span class="label">状态：</span>
          <span class="value status-value" :style="{ color: statusColor }">
            <i :class="statusIcon"></i>
            {{ statusName }}
          </span>
        </div>

        <div v-if="note.notes" class="info-row notes-row">
          <span class="label">备注：</span>
          <span class="value">{{ note.notes }}</span>
        </div>

        <div class="progress-container">
          <div class="progress-label">时间进度</div>
          <div class="progress-bar-container">
            <div
                class="progress-bar-fill"
                :style="{
                width: `${progressPercentage}%`,
                background: statusColor
              }"
            ></div>
          </div>
          <div class="progress-percentage">{{ progressPercentage }}%</div>
        </div>
      </div>

      <template #footer>
        <ElButton type="danger" @click="handleDeleteNote">删除</ElButton>
        <ElButton @click="dialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </div>

  <!-- 状态选择对话框 -->
  <div>
    <ElDialog
        :class="dialogClass"
        v-model="statusSelectVisible"
        title="选择状态"
        :close-on-click-modal="false"
    >
      <div class="status-options">
        <div
            v-for="status in Object.values(Status)"
            :key="status"
            class="status-option"
            :style="{ borderColor: StatusInfo[status].color }"
            @click="updateStatus(status)"
        >
          <div class="status-icon-container" :style="{ backgroundColor: `${StatusInfo[status].color}20` }">
            <i :class="[StatusInfo[status].icon, 'status-icon']" :style="{ color: StatusInfo[status].color }"></i>
          </div>
          <span class="status-name">{{ StatusInfo[status].name }}</span>
        </div>
      </div>

      <template #footer>
        <ElButton style="color: #8a7557; background-color: #c8c7c7" @click="statusSelectVisible = false" round>取消</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
.note-card {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 70px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05),
  0 4px 15px rgba(0, 0, 0, 0.08);
  padding: 14px 16px;
  margin-bottom: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.note-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08),
  0 8px 25px rgba(0, 0, 0, 0.12);
}

.progress-indicator {
  width: 5px;
  height: 50px;
  background-color: rgba(0, 0, 0, 0.06);
  border-radius: 3px;
  margin-right: 16px;
  position: relative;
  overflow: hidden;
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 3px;
  transform-origin: bottom;
  transition: transform 0.6s ease, background 0.3s ease;
}

.due-time {
  min-width: 60px;
  margin-right: 16px;
  text-align: center;
}

.due-time .date {
  font-weight: 650;
  font-size: 16px;
  color: #333;
  letter-spacing: -0.3px;
}

.due-time .time {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
  opacity: 0.85;
}

.content {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 12px;
  letter-spacing: -0.2px;
}

.notes {
  flex: 1;
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 12px;
  opacity: 0.9;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 14px;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
  backdrop-filter: blur(4px);
}

.status-tag i {
  margin-right: 6px;
  font-size: 13px;
}


.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  align-items: flex-start;
}

.label {
  font-weight: 600;
  color: #666;
  min-width: 80px;
}

.value {
  flex: 1;
  color: #333;
}

.status-value {
  display: flex;
  align-items: center;
  gap: 6px;
}

.notes-row {
  flex-direction: column;
  gap: 8px;
}

.notes-row .value {
  padding: 10px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 6px;
  line-height: 1.5;
}

.progress-container {
  margin-top: 20px;
}

.progress-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.progress-bar-container {
  height: 8px;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease, background 0.3s ease;
}

.progress-percentage {
  font-size: 12px;
  color: #666;
  text-align: right;
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

/* 新增状态选择对话框样式 */
.status-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 12px;
}

.status-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.status-option:hover {
  background-color: rgba(0, 0, 0, 0.03);
  transform: translateY(-2px);
}

.status-icon-container {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.status-icon {
  font-size: 24px;
}

.status-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 暗色主题适配 */
.dark .status-option:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.dark .status-name {
  color: #f0f0f0;
}
</style>
