<template>
  <el-row :gutter="20" class="word-query-bar">
    <el-col :span="6">
      <el-select
          v-model="queryForm.category"
          placeholder="请选择单词种类"
          clearable
          @change="handleQueryChange"
      >
        <el-option
            v-for=" (value, key) in categoryOptions"
            :key="key"
            :label="value"
            :value="Number(key)"
        />
      </el-select>
    </el-col>
    <el-col :span="6">
      <el-input
          v-model="queryForm.english"
          placeholder="请输入英文"
          clearable
          @input="handleInputChange"
      />
    </el-col>
    <el-col :span="6">
      <el-input
          v-model="queryForm.chinese"
          placeholder="请输入中文"
          clearable
          @input="handleInputChange"
      />
    </el-col>
    <el-col :span="6">
      <el-button type="primary" @click="handleReset">重置</el-button>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useBaseWordStore } from '@/english/stores';
import type { BaseWordQuery } from '@/english/types/dto/BaseWordQuery';
import { debounce } from 'lodash';
import {ElMessage} from "element-plus";

const baseWordStore = useBaseWordStore();

// 分类选项
const categoryOptions = ref<Map<number, string>>(new Map());
// 查询表单
const queryForm = ref<BaseWordQuery>({
  category: null,
  english: null,
  chinese: null,
});

// 初始化分类数据
const initCategory = async () => {
  const res = await baseWordStore.fetchCategory();
  if (res.code === -1) {
    ElMessage.error(res.message as any)
    return
  }
  categoryOptions.value = res.data;

};

// 防抖处理输入事件，避免频繁查询
const debouncedFetch = debounce(() => {
  // 更新分页查询参数
  baseWordStore.pageQuery.query = { ...queryForm.value };
  // 重置页码为1
  baseWordStore.pageQuery.pageNum = 1;
  // 执行查询
  baseWordStore.fetchWordPage();
}, 300);

// 处理输入框变化
const handleInputChange = () => {
  debouncedFetch();
};

// 处理下拉框变化
const handleQueryChange = () => {
  debouncedFetch();
};

// 重置查询条件
const handleReset = () => {
  queryForm.value = {
    category: null,
    english: null,
    chinese: null,
  };
  // 重置页码并查询
  baseWordStore.pageQuery = {
    ...baseWordStore.pageQuery,
    pageNum: 1,
    query: { ...queryForm.value },
  };
  baseWordStore.fetchWordPage();
};

// 监听查询表单初始值同步
onMounted(async () => {
  await initCategory();
  // 初始化时同步查询条件
  queryForm.value = { ...baseWordStore.pageQuery.query };
  // 执行查询
  await baseWordStore.fetchWordPage();

});

// 监听store中查询条件变化，同步到表单
watch(
    () => baseWordStore.pageQuery.query,
    (newVal) => {
      queryForm.value = { ...newVal };
    },
    { deep: true }
);
</script>

<style scoped>
.word-query-bar {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eff8 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
}

.word-query-bar .el-col {
  display: flex;
  align-items: center;
}

.word-query-bar .el-select,
.word-query-bar .el-input {
  width: 100%;
}



.word-query-bar .el-button {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.word-query-bar .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}
</style>