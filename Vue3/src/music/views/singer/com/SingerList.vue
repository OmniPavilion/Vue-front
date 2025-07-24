<script setup lang="ts">
import { useSingerStore } from "@/music/stores";
import { onMounted, computed } from "vue";
import SingerCard from "@/music/views/singer/com/SingerCard.vue";
import { ElRow, ElCol } from "element-plus";

const singerStore = useSingerStore();

const singerList = computed(() => singerStore.singers.filter(singer => singer.id !== 1));

onMounted(() => {
  singerStore.fetchSingerPage();
});
</script>

<template>
  <div class="singer-list-container">
    <el-row :gutter="20">
      <el-col
          v-for="singer in singerList"
          :key="singer.id"
          :xs="12"
          :sm="8"
          :md="6"
          :lg="4"
          :xl="3"
      >
        <SingerCard :singer="singer"></SingerCard>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.singer-list-container {
  overflow-y: auto;
  padding: 20px;
  height: 100%;
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
