<template>
  <el-pagination
      v-model:current-page="pageQuery.pageNum"
      v-model:page-size="pageQuery.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :small="props.small"
      :disabled="props.disabled"
      :background="props.background"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
  />
</template>

<script setup lang="ts">
import { useBaseWordStore } from '@/english/stores';
import { storeToRefs } from 'pinia';

// 定义组件属性
interface Props {
  small?: boolean;
  disabled?: boolean;
  background?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  small: false,
  disabled: false,
  background: true,
});

// 使用 store
const baseWordStore = useBaseWordStore();
const { pageQuery, total } = storeToRefs(baseWordStore);

// 处理每页数量变化
const handleSizeChange = (newSize: number) => {
  pageQuery.value.pageSize = newSize;
  pageQuery.value.pageNum = 1; // 重置到第一页
  baseWordStore.fetchWordPage();
};

// 处理当前页码变化
const handleCurrentChange = (newPage: number) => {
  pageQuery.value.pageNum = newPage;
  baseWordStore.fetchWordPage();
};

// 暴露方法供父组件调用
defineExpose({
  refresh: () => baseWordStore.fetchWordPage(),
});
</script>

<style scoped>
.el-pagination {
  margin-top: 24px;
  padding: 16px 20px;
  justify-content: center;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;
}

.el-pagination:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.el-pagination :deep(.btn-prev),
.el-pagination :deep(.btn-next),
.el-pagination :deep(.el-pager li) {
  min-width: 36px;
  height: 36px;
  line-height: 36px;
  border-radius: 8px;
  margin: 0 4px;
  border: 1px solid #e6e9ed;
  background: #ffffff;
  transition: all 0.2s ease;
}

.el-pagination :deep(.btn-prev:hover),
.el-pagination :deep(.btn-next:hover),
.el-pagination :deep(.el-pager li:hover) {
  color: #409EFF;
  border-color: #409EFF;
  transform: translateY(-1px);
}

.el-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409EFF 0%, #64b5ff 100%);
  color: #ffffff;
  border-color: #409EFF;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.el-pagination :deep(.el-pagination__sizes) {
  margin: 0 16px;
}

.el-pagination :deep(.el-pagination__sizes .el-input__inner) {
  border-radius: 8px;
  border: 1px solid #e6e9ed;
  transition: border-color 0.2s ease;
}

.el-pagination :deep(.el-pagination__sizes .el-input__inner:focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.el-pagination :deep(.el-pagination__jump) {
  margin-left: 16px;
  color: #5a6c82;
}

.el-pagination :deep(.el-pagination__jump .el-input__inner) {
  border-radius: 8px;
  border: 1px solid #e6e9ed;
  transition: border-color 0.2s ease;
}

.el-pagination :deep(.el-pagination__jump .el-input__inner:focus) {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .word-query-bar .el-col {
    margin-bottom: 12px;
  }

  .word-table-container {
    padding: 16px;
    overflow-x: auto;
  }

  .el-pagination {
    flex-wrap: wrap;
    gap: 12px;
  }

  .el-pagination :deep(.el-pagination__total),
  .el-pagination :deep(.el-pagination__sizes),
  .el-pagination :deep(.el-pagination__jump) {
    margin: 4px 0;
  }
}
</style>