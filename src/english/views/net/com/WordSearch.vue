<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useWordNetStore } from '@/english/stores';
import type { NetWordQuery } from '@/english/types/dto/NetWordQuery';
import { debounce } from 'lodash';
import {ElMessage} from "element-plus";

const wordNetStore = useWordNetStore();

// 分类选项
const posOptions = ref<Map<number, string>>(new Map());
// 查询表单
const queryForm = ref<NetWordQuery>({
  posId: null,
  word: null,
});

// 初始化分类数据
const initPos = async () => {
  const res = await wordNetStore.fetchPosDict();
  if (res.code === -1) {
    ElMessage.error(res.message as any)
    return
  }
  posOptions.value = res.data;

};

// 防抖处理输入事件，避免频繁查询
const debouncedFetch = debounce(() => {
  // 更新分页查询参数
  wordNetStore.pageQuery.query = { ...queryForm.value };
  // 重置页码为1
  wordNetStore.pageQuery.pageNum = 1;
  // 执行查询
  wordNetStore.fetchWordNetPage();
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
    posId: null,
    word: null,
  };
  // 重置页码并查询
  wordNetStore.pageQuery = {
    ...wordNetStore.pageQuery,
    pageNum: 1,
    query: { ...queryForm.value },
  };
  wordNetStore.fetchWordNetPage();
};

// 监听查询表单初始值同步
onMounted(async () => {
  await initPos();
  // 初始化时同步查询条件
  queryForm.value = { ...wordNetStore.pageQuery.query };
  // 执行查询
  await wordNetStore.fetchWordNetPage();

});

// 监听store中查询条件变化，同步到表单
watch(
    () => wordNetStore.pageQuery.query,
    (newVal) => {
      queryForm.value = { ...newVal };
    },
    { deep: true }
);
</script>

<template>
  <el-row :gutter="20" class="word-query-bar">
    <el-col :span="6">
      <el-select
          v-model="queryForm.posId"
          placeholder="请选择单词种类"
          clearable
          @change="handleQueryChange"
      >
        <el-option
            v-for=" (value, key) in posOptions"
            :key="key"
            :label="value"
            :value="Number(key)"
        />
      </el-select>
    </el-col>
    <el-col :span="6">
      <el-input
          v-model="queryForm.word"
          placeholder="请输入英文"
          clearable
          @input="handleInputChange"
      />
    </el-col>
    <el-col :span="6">
      <el-button type="primary" @click="handleReset">重置</el-button>
    </el-col>
  </el-row>
</template>


<style scoped>
.word-query-bar {
  margin-bottom: 5px;
  padding: 10px 20px;
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