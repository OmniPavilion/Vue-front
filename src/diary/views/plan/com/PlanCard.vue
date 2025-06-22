<script setup lang="ts">
import {computed, onMounted, ref} from 'vue';
import {Status, StatusInfo} from "@/diary/types/vo/Status";
import type {PlanVO} from "@/diary/types/vo/PlanVO";
import {ElButton, ElDialog, ElMessage, ElMessageBox} from 'element-plus';
import {usePlanStore} from "@/diary/stores/stores/planStore";
import type {PlanTaskVO} from "@/diary/types/vo/PlanTaskVO";
import {Close, Edit, Plus} from '@element-plus/icons-vue'

const props = defineProps<{
  plan: PlanVO;
  theme?: 'classic' | 'modern' | 'dark' | 'vintage';
}>();

// 根据主题获取Dialog样式
const dialogClass = computed(() => {
  return `note-dialog ${props.theme || 'vintage'}`;
});

// 状态对话框相关
const statusDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const taskDialogVisible = ref(false);
const currentTask = ref<PlanTaskVO | null>(null);
const editDialogVisible = ref(false);
const currentTaskIndex = ref(-1);
const planStore = usePlanStore();

// 计算状态相关信息
const statusInfo = computed(() => StatusInfo[props.plan.status]);
const statusName = computed(() => statusInfo.value.name);
const statusIcon = computed(() => statusInfo.value.icon);
const statusColor = computed(() => statusInfo.value.color);

// 计算任务数量
const taskCount = computed(() => props.plan.tasks?.length || 0);

// 格式化日期范围
const formattedDateRange = computed(() => {
  const start = props.plan.startDate;
  const end = props.plan.endDate;

  if (start && end) {
    return `${start} 至 ${end}`;
  } else if (start) {
    return `从 ${start} 开始`;
  } else if (end) {
    return `截止 ${end}`;
  }
  return '无日期范围';
});

// 打开状态选择对话框
const openStatusDialog = (index: number) => {
  if (props.plan.status === Status.EXPIRED) {
    ElMessage.warning("计划以逾期, 无法修改" as any)
    return
  }
  currentTaskIndex.value = index;
  statusDialogVisible.value = true;
};

// 打开详情对话框
const openDetailDialog = () => {
  detailDialogVisible.value = true;
};

// 处理状态变更
const handleStatusChange = async (newStatus: Status) => {

  if (currentTaskIndex.value !== -1) {
    props.plan.tasks[currentTaskIndex.value].status = newStatus;
  } else {
    props.plan.status = newStatus;
  }

  if (newStatus === Status.EXPIRED || newStatus === Status.COMPLETED || newStatus === Status.IN_PROGRESS) {
    ElMessage.warning('请选择一个有效的状态' as any);
    return
  }

  statusDialogVisible.value = false;
  const res = await planStore.updatePlan(props.plan)
  if (!res) {
    ElMessage.error('状态更新失败' as any);
    return;
  }
  ElMessage.success(`状态已更新为: ${StatusInfo[newStatus].name}` as any);
};

// 删除任务
const handleDeletePlan = () => {
  ElMessageBox.confirm(
      '确定要删除此计划吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    const id = props.plan.id
    if (id === undefined) {
      ElMessage.error('计划不存在' as any);
      return;
    }
    const res = await planStore.deletePlan(id);
    await planStore.fetchPlansByPage()
    if (res.code !== 1) {
      ElMessage.error('计划删除失败' as any);
      return;
    } else {
      ElMessage.success('计划已删除' as any);
    }
  }).catch(() => {
    ElMessage.info('已取消删除' as any);
  });
}

// 添加任务编辑方法
const openTaskDialog = (index: number) => {
  if (props.plan.status === Status.EXPIRED) {
    ElMessage.warning("无法为逾期的计划编辑任务" as any)
    return
  }
  currentTask.value = props.plan.tasks[index];
  currentTaskIndex.value = index;
  taskDialogVisible.value = true;
};

// 处理任务编辑方法
const handleTaskEdit = async () => {

  if (!currentTask.value) return;
  props.plan.tasks[currentTaskIndex.value] = currentTask.value;
  const res = await planStore.updatePlan(props.plan)
  if (res.code === -1) {
    ElMessage.error('任务更新失败' as any);
  }
  ElMessage.success('任务已更新' as any);
  taskDialogVisible.value = false;
  currentTask.value = null
  currentTaskIndex.value = -1
}

const handDeleteTask = () => {
  ElMessageBox.confirm(
      '确定要删除此任务吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    if (currentTaskIndex.value !== -1) {
      props.plan.tasks.splice(currentTaskIndex.value, 1);
      const res = await planStore.updatePlan(props.plan);
      if (!res) {
        ElMessage.error('任务删除失败' as any);
        return;
      }
      ElMessage.success('任务已删除' as any);
      taskDialogVisible.value = false;
      currentTask.value = null;
      currentTaskIndex.value = -1;
    }
  }).catch(() => {
    ElMessage.info('已取消删除' as any);
  });
};

const addTask = () => {
  if (props.plan.status === Status.EXPIRED) {
    ElMessage.warning("无法为逾期的计划添加任务" as any)
    return
  }

  props.plan.tasks.push({
    title: '默认标题',
    description: '',
    status: Status.PENDING
  })

  ElMessage.success('任务已添加' as any);
}

// 计算时间进度百分比
const timeProgress = computed(() => {
  if (!props.plan.startDate || !props.plan.endDate) return 0;

  const start = new Date(props.plan.startDate);
  const end = new Date(props.plan.endDate);
  const now = new Date();

  // 如果当前时间早于开始时间，进度为0
  if (now < start) return 0;
  // 如果当前时间晚于结束时间，进度为100
  if (now > end) return 100;

  const totalDuration = end.getTime() - start.getTime();
  const elapsedDuration = now.getTime() - start.getTime();

  return Math.round((elapsedDuration / totalDuration) * 100);
});

// 计算任务进度
const completedTasks = computed(() => {
  return props.plan.tasks?.filter(task => task.status === Status.COMPLETED).length || 0;
});

const taskProgress = computed(() => {
  if (taskCount.value === 0) return 0;
  return Math.round((completedTasks.value / taskCount.value) * 100);
});

// 格式化日期显示
const formatDate = (dateString?: string) => {
  if (!dateString) return '未设置';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {month: 'short', day: 'numeric'});
};

// 打开编辑对话框
const openEditDialog = () => {
  if (props.plan.status === Status.EXPIRED) {
    ElMessage.warning("逾期的任务无法编辑" as any)
    return
  }
  editDialogVisible.value = true;
};

// 处理编辑保存
const handleEditSave = async () => {
  const res = await planStore.updatePlan(props.plan);
  if (!res) {
    ElMessage.error('更新失败' as any);
    return;
  }

  ElMessage.success('计划已更新' as any);
  editDialogVisible.value = false;
};

// 禁用结束日期早于开始日期
const disabledEndDate = (time: Date) => {
  return time < new Date(props.plan.startDate);
};

// 禁用开始日期晚于结束日期
const disabledStartDate = (time: Date) => {
  return props.plan.endDate && time > new Date(props.plan.endDate);
};

onMounted(() => {
  // 判断是否截止
  if (props.plan.endDate && new Date(props.plan.endDate) < new Date()) {
    props.plan.status = Status.EXPIRED
    props.plan.tasks.forEach(task => {
      task.status = Status.EXPIRED
    })
    planStore.updatePlan(props.plan)
  } else if (props.plan.startDate && new Date(props.plan.startDate) > new Date()) {
    props.plan.status = Status.PENDING
    props.plan.tasks.forEach(task => {
      task.status = Status.PENDING
    })
    planStore.updatePlan(props.plan)
  } else {
    props.plan.status = Status.IN_PROGRESS
    props.plan.tasks.forEach(task => {
      task.status = Status.IN_PROGRESS
    })
    planStore.updatePlan(props.plan)
  }
})
</script>

<template>
  <el-card
      @click="openDetailDialog"
      class="plan-card" :style="{ borderLeft: `4px solid ${statusColor}` }">
    <div class="plan-header">
      <div class="plan-title">{{ plan.title }}</div>
      <!--       添加编辑按钮 -->
      <div>
        <el-button
            @click.stop="openEditDialog"
            class="edit-btn "
            circle
        >
          <el-icon>
            <Edit/>
          </el-icon>
        </el-button>
      </div>

      <!--       状态标签 -->
      <div
          class="status-tag"
          :style="{
        backgroundColor: `${statusColor}15`,
        color: statusColor,
        border: `1px solid ${statusColor}30`
      }"
          @click.stop="openStatusDialog(-1)"
      >
        <i :class="statusIcon"></i>
        <span>{{ statusName }}</span>
      </div>
    </div>

    <div class="plan-meta">
      <div class="meta-item">
        <i class="el-icon-tickets"></i>
        <span>{{ taskCount }} 个任务</span>
      </div>
      <div class="meta-item">
        <i class="el-icon-date"></i>
        <span>{{ formattedDateRange }}</span>
      </div>
    </div>

    <el-divider></el-divider>

    <div class="plan-content">
      <p>{{ plan.content }}</p>
    </div>

    <el-button
        round
        class="delete-plan-btn"
        @click.stop="handleDeletePlan">
      <el-icon>
        <Close/>
      </el-icon>
    </el-button>
  </el-card>

  <!-- 状态选择对话框 -->
  <div>
    <ElDialog
        :class="dialogClass"
        v-model="statusDialogVisible"
        title="选择状态"
        :close-on-click-modal="false"
    >
      <div class="status-options">
        <div
            v-for="status in Object.values(Status)"
            :key="status"
            class="status-option"
            :style="{ borderColor: StatusInfo[status].color }"
            @click="handleStatusChange(status)"
        >
          <div class="status-icon-container" :style="{ backgroundColor: `${StatusInfo[status].color}20` }">
            <i :class="[StatusInfo[status].icon, 'status-icon']" :style="{ color: StatusInfo[status].color }"></i>
          </div>
          <span class="status-name">{{ StatusInfo[status].name }}</span>
        </div>
      </div>

      <template #footer>
        <ElButton style="color: #8a7557; background-color: #c8c7c7" @click="statusDialogVisible = false" round>取消
        </ElButton>
      </template>
    </ElDialog>
  </div>

  <!-- 计划详情对话框 -->
  <div>
    <ElDialog
        :class="dialogClass"
        v-model="detailDialogVisible"
        :close-on-click-modal="false"
    >
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">{{ plan.title }}</span>
          <div
              class="status-tag"
              @click="openStatusDialog(-1)"
              :style="{
            backgroundColor: `${statusColor}15`,
            color: statusColor,
            border: `1px solid ${statusColor}30`
          }"
          >
            <i :class="statusIcon"></i>
            <span>{{ statusName }}</span>
          </div>
        </div>
      </template>

      <div class="plan-detail">
        <!-- 进度条区域 -->
        <div class="progress-section">
          <!-- 时间进度 -->
          <div class="progress-item">
            <div class="progress-header">
              <span>时间进度</span>
              <span class="progress-percentage">{{ timeProgress }}%</span>
            </div>
            <el-progress
                :percentage="timeProgress"
                :color="statusColor"
                :show-text="false"
                :stroke-width="8"
            />
            <div class="progress-date">
              <span>{{ formatDate(plan.startDate) }}</span>
              <span>{{ formatDate(plan.endDate) }}</span>
            </div>
          </div>

          <!-- 任务进度 -->
          <div class="progress-item" v-if="taskCount > 0">
            <div class="progress-header">
              <span>任务进度</span>
              <span class="progress-percentage">{{ taskProgress }}%</span>
            </div>
            <el-progress
                :percentage="taskProgress"
                :color="statusColor"
                :show-text="false"
                :stroke-width="8"
            />
            <div class="progress-stats">
              <span>已完成: {{ completedTasks }} / {{ taskCount }}</span>
            </div>
          </div>
        </div>

        <!-- 日期和任务数量信息 -->
        <div class="detail-meta">
          <div class="detail-date">
            <i class="el-icon-date"></i>
            <span>{{ formattedDateRange }}</span>
          </div>
          <div class="detail-tasks">
            <i class="el-icon-tickets"></i>
            <span>{{ taskCount }} 个任务</span>
          </div>
        </div>

        <!-- 计划内容 -->
        <div class="detail-content">
          <h4>计划内容</h4>
          <div class="content-box">
            <div v-if="plan.content">
              <p>{{ plan.content }}</p>
            </div>
            <div v-else>
              <p>暂无内容描述</p>
            </div>
          </div>
        </div>

        <!-- 任务列表 -->
        <div class="detail-task-list">
          <div class="task-list-header">
            任务列表
            <el-button
                class="add-task-btn"
                round
                @click="addTask"
            >
              <el-icon>
                <Plus/>
              </el-icon>
            </el-button>
          </div>
          <div class="log-page scroll-container">
            <div class="task-list" v-if="taskCount > 0">
              <div class="task-item" @click="openTaskDialog(index)" v-for="(task, index) in plan.tasks" :key="index">
                <div class="task-main">
                  <div class="task-title">{{ index + 1 }}. {{ task.title }}</div>
                  <div class="task-description" v-if="task.description">{{ task.description }}</div>
                </div>
                <div class="task-meta">
                  <div
                      @click.stop="openStatusDialog(index)"
                      class="status-tag"
                      :style="{
                  backgroundColor: `${StatusInfo[task.status].color}15`,
                  color: StatusInfo[task.status].color,
                  border: `1px solid ${StatusInfo[task.status].color}30`
                }"
                  >
                    <i :class="StatusInfo[task.status].icon"></i>
                    <span>{{ StatusInfo[task.status].name }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="content-box">
              <p>暂无任务</p>

            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <ElButton style="color: #8a7557; background-color: #c8c7c7" @click="detailDialogVisible = false" round>关闭
        </ElButton>
      </template>
    </ElDialog>
  </div>

  <!-- 任务编辑对话框 -->
  <div>
    <ElDialog
        :class="dialogClass"
        v-model="taskDialogVisible"
        title="编辑任务"
        :close-on-click-modal="false"
    >
      <div class="task-edit-form" v-if="currentTask">
        <el-form label-width="80px">
          <el-form-item label="任务标题">
            <el-input v-model="currentTask.title" class="theme-select"/>
          </el-form-item>

          <el-form-item label="任务描述">
            <el-input
                class="theme-input"
                v-model="currentTask.description"
                type="textarea"
                :rows="3"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button type="primary" @click="handDeleteTask" class="delete-btn" round>删除</el-button>
        <el-button type="primary" @click="taskDialogVisible = false" class="btn" round>取消</el-button>
        <el-button type="primary" @click="handleTaskEdit" class="btn" round>保存</el-button>
      </template>
    </ElDialog>
  </div>

  <div>
    <!-- 添加编辑对话框 -->
    <ElDialog
        :class="dialogClass"
        v-model="editDialogVisible"
        title="编辑计划"
        :close-on-click-modal="false"
    >
      <el-form :model="props.plan" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="props.plan.title" class="theme-input"/>
        </el-form-item>

        <el-form-item label="内容">
          <el-input
              class="theme-input"
              v-model="props.plan.content"
              type="textarea"
              :rows="4"
          />
        </el-form-item>

        <el-form-item label="开始日期">
          <el-date-picker
              class="theme-date-picker"

              v-model="props.plan.startDate"
              type="date"
              placeholder="选择开始日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledStartDate"
          />
        </el-form-item>

        <el-form-item label="结束日期">
          <el-date-picker
              class="theme-date-picker"
              v-model="props.plan.endDate"
              type="date"
              placeholder="选择结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledEndDate"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSave">保存</el-button>
      </template>
    </ElDialog>
  </div>

</template>

<style scoped>
.delete-plan-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 25px;
  height: 25px;
  border: transparent;
}

.delete-plan-btn:hover {
  background-color: transparent;
  color: rgba(255, 0, 0, 0.8);
}

.btn {
  border: #b8a07a;
  background-color: #b8a07a;
}

.btn:hover {
  background-color: #a38b6a;
  border: #a38b6a;
}

.delete-btn {
  background-color: #5a5a5a;
  border: #3a3a3a;
}

.delete-btn:hover {
  background-color: #5a5555;
  color: rgba(132, 143, 156, 0.8);
}

.plan-card {
  position: relative;
  width: 100%;
  max-width: 400px;
  min-height: 200px;
  margin: 10px;
  transition: all 0.3s ease;
  cursor: default;
}

.plan-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.plan-header {
  display: flex;
  justify-content: flex-start; /* 改为从左开始排列 */
  align-items: center;
  margin-bottom: 12px;
  gap: 10px; /* 添加元素间距 */
}

.plan-title {
  flex: 1; /* 占据剩余空间 */
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
  text-align: left; /* 确保文本左对齐 */
}

.status-tag {
  float: right;
  display: inline-flex;
  align-items: center;
  padding: 3px 8px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
  backdrop-filter: blur(4px);
}

.status-tag i {
  margin-right: 6px;
  font-size: 13px;
}

.plan-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.plan-content {
  color: #555;
  font-size: 14px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
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

/* 响应式调整 */
@media (max-width: 768px) {
  .status-options {
    flex-direction: column;
    align-items: center;
  }

  .status-options .el-button {
    width: 100%;
    margin: 4px 0;
  }
}

/* 对话框头部样式 */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

/* 元信息布局 */
.detail-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.detail-date,
.detail-tasks {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 内容区域样式 */
.detail-content {
  margin-bottom: 20px;
}

.detail-content h4,
.detail-task-list h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.content-box {
  background-color: rgba(0, 0, 0, 0.03);
  padding: 12px 16px;
  border-radius: 4px;
  line-height: 1.6;
}

/* 任务列表样式 */
.add-task-btn {
  width: 25px;
  height: 25px;
  background-color: #d5b88b;
  color: #555555;
}

.add-task-btn:hover {
  background-color: #887459;
}

.task-list-header {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.task-list {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color);
}

.task-item:last-child {
  border-bottom: none;
}

.task-main {
  flex: 1;
  min-width: 0;
}

.task-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.task-description {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: pre-wrap;
}

.task-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  margin-left: 16px;
  min-width: 120px;
}

.task-due {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

/* 暗色主题适配 */
.dark .content-box {
  background-color: rgba(255, 255, 255, 0.05);
}

.dark .task-list {
  border-color: var(--el-border-color);
}

.dark .task-item {
  border-color: var(--el-border-color);
}

/* 进度条区域样式 */
.progress-section {
  margin-bottom: 20px;
  padding: 16px;
  background-color: rgba(0, 0, 0, 0.03);
  border-radius: 6px;
}

.progress-item {
  margin-bottom: 16px;
}

.progress-item:last-child {
  margin-bottom: 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.progress-percentage {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.progress-date {
  display: flex;
  justify-content: space-between;
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.progress-stats {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  text-align: right;
}

/* 暗色主题适配 */
.dark .progress-section {
  background-color: rgba(255, 255, 255, 0.05);
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

/* 添加编辑按钮样式 */
.edit-btn {
  float: left; /* 添加浮动 */
  margin-right: 8px; /* 添加右边距 */
  background-color: transparent;
  border: none;
  color: #666;
  padding: 6px;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  color: #b8a07a;
  background-color: rgba(184, 160, 122, 0.1);
}

/* 暗色主题适配 */
.dark .edit-btn {
  color: #aaa;
}

.dark .edit-btn:hover {
  color: #b8a07a;
  background-color: rgba(184, 160, 122, 0.2);
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


.scroll-container {
  max-height: 250px;
  overflow-y: auto;
  padding-right: 12px;

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
