<script setup lang="ts">

import WordSearch from "@/english/views/book/com/WordSearch.vue";
import WordForm from "@/english/views/book/com/WordForm.vue";
import WordPagination from "@/english/views/book/com/WordPagination.vue";
</script>

<template>
  <div class="base-word-div">
    <div class="layout-container">
      <!-- 搜索区域 -->
      <div class="search-section">
        <WordSearch></WordSearch>
      </div>

      <!-- 表格区域 -->
      <div class="table-section">
        <WordForm></WordForm>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-section">
        <WordPagination></WordPagination>
      </div>
    </div>
  </div>
</template>

<style scoped>
.base-word-div {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
}

.layout-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 16px;
}

.search-section {
  flex-shrink: 0;
}

.table-section {
  flex: 1;
  min-height: 0; /* 重要：防止表格溢出 */
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.pagination-section {
  flex-shrink: 0;
  margin-top: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .base-word-div {
    padding: 12px;
  }

  .layout-container {
    gap: 12px;
  }
}
</style>