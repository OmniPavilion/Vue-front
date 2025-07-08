<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useMusicStore } from '@/music/stores';

const musicStore = useMusicStore();
const layout = ref('total, sizes, prev, pager, next, jumper');

// 处理页码变化
const handleCurrentChange = (page: number) => {
  musicStore.pageQuery.pageNum = page;
  musicStore.fetchMusicPage();
};

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  musicStore.pageQuery.pageSize = size;
  musicStore.pageQuery.pageNum = 1; // 重置到第一页
  musicStore.fetchMusicPage();
};

const checkScreenSize = () => {
  if (window.innerWidth < 768) {
    layout.value = 'prev, pager, next';
  } else if (window.innerWidth < 992) {
    layout.value = 'total, prev, pager, next';
  } else {
    layout.value = 'total, sizes, prev, pager, next, jumper';
  }
};

onMounted(() => {
  checkScreenSize();
  window.addEventListener('resize', checkScreenSize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', checkScreenSize);
});
</script>

<template>
  <div class="pagination-container">
    <el-pagination
        v-model:current-page="musicStore.pageQuery.pageNum"
        v-model:page-size="musicStore.pageQuery.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="musicStore.total"
        :layout="layout"
        background
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
    />
  </div>
</template>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 5px 0;
  border-radius: 4px;
  width: 100%;
  min-width: 300px;
}

/* 调整分页条整体高度 */
:deep(.el-pagination) {
  height: 40px; /* 设置整体高度 */
}

</style>
