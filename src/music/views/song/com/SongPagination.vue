<script setup lang="ts">
import { useMusicStore } from '@/music/stores';

const musicStore = useMusicStore();


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

</script>

<template>
  <div class="pagination-container">
    <el-pagination
        v-model:current-page="musicStore.pageQuery.pageNum"
        v-model:page-size="musicStore.pageQuery.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="musicStore.total"
        layout="total, sizes, prev, pager, next, jumper"
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
  padding: 10px 0;
  border-radius: 4px;
}
</style>
