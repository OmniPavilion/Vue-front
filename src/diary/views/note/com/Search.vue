<script setup lang="ts">
import {ref, watch, onMounted} from 'vue';
import {useNoteStore} from "@/diary/stores";
import {debounce} from 'lodash-es';
import {dayjs, ElMessage} from "element-plus";
import {Status, StatusOptions} from "@/diary/types/vo/Status";
import type {NoteVO} from "@/diary/types/vo/NoteVO";

const noteStore = useNoteStore();


// 添加日志对话框相关
const dialogVisible = ref(false);
const newNoteData = ref<NoteVO>({
  content: '默认事项',
  notes: '',
  status: Status.PENDING,
  dueDate: new Date().toISOString().slice(0, 10),
  dueTime: new Date().toISOString().slice(0, 10)
});

// 获取表单引用
const formRef = ref();

// 防抖自动查询函数
const autoSearch = debounce(() => {
  noteStore.fetchNotePage();
}, 300);

const addLog = async () => {
  dialogVisible.value = true;
};

const confirmAddNote = async () => {
  // 表单验证
  try {
    await formRef.value.validate();

    const res = await noteStore.createNote({
      ...newNoteData.value,
      dueTime: dayjs(newNoteData.value.dueTime).format('HH:mm:ss')
    });

    if (res.code === -1) {
      ElMessage.error(res.message as any);
      return;
    }

    ElMessage.success('添加成功' as any);
    dialogVisible.value = false;
    await noteStore.fetchNotePage();

    // 重置表单
    newNoteData.value = {
      content: '默认事项',
      notes: '',
      status: Status.PENDING,
      dueDate: new Date().toISOString().slice(0, 10),
      dueTime: new Date().toISOString().slice(0, 10)
    };

  } catch (error) {
    // 验证失败会自动显示错误信息
    console.error('表单验证失败:', error);
  }
};

// 表单验证规则
const rules = {
  content: [
    {required: true, message: '请输入内容', trigger: 'blur'},
    {min: 3, message: '内容长度不能少于3个字符', trigger: 'blur'}
  ],
  dueDate: [
    {required: true, message: '请选择截止日期', trigger: 'change'}
  ],
  dueTime: [
    {required: true, message: '请选择截止时间', trigger: 'change'}
  ]
};

// 监听表单变化自动查询
watch(
    () => [noteStore.pageQuery.query],
    () => {
      autoSearch();
    },
    {deep: true}
);

// 初始化加载数据
onMounted(() => {
  noteStore.fetchNotePage()
});
</script>

<template>
  <div class="log-search-container">
    <div class="log-search">
      <el-form
          :inline="true"
          :model="noteStore.pageQuery"
          class="search-form">
        <el-form-item label="状态" class="form-item" prop="status">
          <el-select
              v-model="noteStore.pageQuery.query"
              style="width: 75px;"
              placeholder="状态"
              clearable
              @change="autoSearch"
              class="theme-select"
          >
            <el-option
                v-for="category in StatusOptions"
                :key="category.value"
                :label="category.label"
                :value="category.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="数据量" class="form-item">
          <el-select
              v-model="noteStore.pageQuery.pageSize"
              style="width: 90px;"
              placeholder="数据量"
              clearable
              @change="autoSearch"
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

        <el-form-item class="form-item">
          <el-button type="primary" @click="addLog" class="add-button theme-button">
            添加代表事项
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 添加日志对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="添加新计划"
        width="500px"
        class="theme-dialog">
      <el-form :rules="rules"
               ref="formRef"
               :model="newNoteData"
               label-width="80px">
        <el-form-item label="内容" required prop="content">
          <el-input
              v-model="newNoteData.content"
              type="textarea"
              :rows="3"
              placeholder="请输入内容"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="newNoteData.status" placeholder="请选择状态">
            <el-option
                v-for="category in StatusOptions"
                :key="category.value"
                :label="category.label"
                :value="category.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="截至日期" prop="dueDate">
          <el-date-picker
              v-model="newNoteData.dueDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="theme-date-picker"
          />
        </el-form-item>
        <el-form-item label="截至时间" prop="dueTime">
          <el-time-picker
              v-model="newNoteData.dueTime"
              placeholder="选择时间"
              format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input
              v-model="newNoteData.notes"
              type="textarea"
              :rows="2"
              placeholder="可选备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="theme-button">取消</el-button>
        <el-button type="primary" @click="confirmAddNote" class="theme-button">确认</el-button>
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
</style>
