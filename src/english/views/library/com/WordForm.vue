<script setup lang="ts">
import { computed } from 'vue';
import { useBaseWordStore, useAiChatStore } from '@/english/stores';

// 使用Pinia store
const baseWordStore = useBaseWordStore();
const aiChatStore = useAiChatStore();

// 计算属性获取单词列表和加载状态
const words = computed(() => baseWordStore.words);
const loading = computed(() => baseWordStore.loading);

const handleSearch = (english: string) => {
  aiChatStore.chat( "查询单词" + english)
  setTimeout(  aiChatStore.openChat, 20)
};

const handleAdd = (english: string) => {
  aiChatStore.chat( "添加单词" + english)
  setTimeout(  aiChatStore.openChat, 20)
};
</script>

<template>
  <div class="word-table-container">
    <el-table
        :data="words"
        v-loading="loading"
        style="width: 100%; height: 700px;"
        :default-sort="{ prop: 'id', order: 'descending' }"
        stripe
    >
      <el-table-column
          label="序号"
          width="80"
          type="index"
          :index="(index: number) => index + 1"
      />
      <el-table-column
          prop="english"
          label="英文"
          min-width="120"
          sortable
      />
      <el-table-column
          prop="chinese"
          label="中文"
          min-width="120"
          sortable
      />
      <el-table-column
          prop="phonetic"
          label="音标"
          min-width="150"
      >
        <template #default="{ row }">
          <span v-if="row.phonetic">/{{ row.phonetic }}/</span>
          <span v-else class="no-phonetic">---</span>
        </template>
      </el-table-column>
      <el-table-column
          label="操作"
          width="180"
          fixed="right"
      >
        <template #default="{ row }">
          <div class="action-buttons">
            <el-button
                size="small"
                @click="handleSearch(row.english)"
                class="search-btn"
            >
              查询
            </el-button>
            <el-button
                size="small"
                type="primary"
                @click="handleAdd(row.english)"
                class="add-btn"
            >
              添加
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>


<style scoped>
.word-table-container {
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  transition: box-shadow 0.3s ease;
}

.word-table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.word-table-container :deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

.word-table-container :deep(.el-table__header) {
  background: linear-gradient(135deg, #f8f9fc 0%, #e9ecef 100%);
}

.word-table-container :deep(.el-table th) {
  background: transparent;
  color: #2c3e50;
  font-weight: 600;
  height: 52px;
  border-bottom: 2px solid #e8eff8;
}

.word-table-container :deep(.el-table td) {
  padding: 16px 0;
  border-bottom: 1px solid #f0f4f8;
  transition: background-color 0.2s ease;
}

.word-table-container :deep(.el-table tr:hover td) {
  background-color: #f8fbff !important;
}

.word-table-container :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafcff;
}

.word-table-container :deep(.el-table .cell) {
  padding: 0 16px;
  font-size: 14px;
  color: #34495e;
}

.word-table-container :deep(.el-table .no-phonetic) {
  color: #a0a8b8;
  font-style: italic;
  font-size: 13px;
}

.word-table-container :deep(.el-loading-mask) {
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.9);
}

/* 添加操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.search-btn {
  background: linear-gradient(135deg, #f8f9fc 0%, #e9ecef 100%);
  border: 1px solid #dcdfe6;
  color: #606266;
}

.search-btn:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  border-color: #c0c4cc;
  color: #409EFF;
}

.add-btn {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
}

.add-btn:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2a6cb5 100%);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.4);
}

/* 调整表格单元格内边距 */
.word-table-container :deep(.el-table td) {
  padding: 12px 0;
}
</style>