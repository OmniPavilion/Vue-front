<script setup lang="ts">
import { useSingerStore } from "@/music/stores";
import { onMounted } from "vue";
import SingerCard from "@/music/views/singer/com/SingerCard.vue";
import { ElRow, ElCol, ElSkeleton } from "element-plus";

const singerStore = useSingerStore();

onMounted(() => {
  singerStore.fetchSingerPage();
});
</script>

<template>
  <div class="singer-list-container">
    <!-- Loading state -->
    <el-skeleton :loading="!singerStore.total" animated>
      <template #template>
        <el-row :gutter="20">
          <el-col
              v-for="i in 8"
              :key="i"
              :xs="12"
              :sm="8"
              :md="6"
              :lg="4"
              :xl="3"
          >
            <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
            <el-skeleton-item variant="text" style="width: 80%" />
          </el-col>
        </el-row>
      </template>

      <!-- Content when loaded -->
      <el-row :gutter="20">
        <el-col
            v-for="singer in singerStore.singers"
            :key="singer.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
            :xl="3"
        >
          <SingerCard :singer="singer" />
        </el-col>
      </el-row>
    </el-skeleton>
  </div>
</template>

<style scoped>
.singer-list-container {
  padding: 20px;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .singer-list-container {
    padding: 10px;
  }
}

/* Add hover effect for better interactivity */
:deep(.el-col) {
  transition: transform 0.3s ease;
  margin-bottom: 20px;
}

:deep(.el-col:hover) {
  transform: translateY(-5px);
}
</style>
