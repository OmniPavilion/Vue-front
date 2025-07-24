<script setup lang="ts">
import { useSingerStore } from "@/music/stores";
import { onMounted, ref } from "vue";
import SingerSideCard from "@/music/views/singerSide/com/SingerSideCard.vue";
import { ElLoading, ElEmpty } from "element-plus";

const singerStore = useSingerStore();
const loading = ref(false);
let loadingInstance: ReturnType<typeof ElLoading.service>;

onMounted(async () => {
  loadingInstance = ElLoading.service({
    target: '.list-container',
    text: '加载中...'
  });
  loading.value = true;

  try {
    await singerStore.fetchSingerPage();
  } finally {
    loading.value = false;
    loadingInstance.close();
  }
});
</script>

<template>
  <div class="singer-side-list">
    <div class="list-header">
      <h3 class="list-title">歌手列表</h3>
      <span class="list-count">共 {{ singerStore.total }} 位歌手</span>
    </div>

    <el-scrollbar class="list-container">
      <template v-if="!loading && singerStore.singers.length > 0">
        <SingerSideCard
            v-for="singer in singerStore.singers"
            :key="singer.id"
            :singer="singer"
            class="singer-item"
        />
      </template>

      <el-empty v-if="!loading && singerStore.singers.length === 0" description="暂无歌手数据" />
    </el-scrollbar>
  </div>
</template>

<style scoped lang="scss">
.singer-side-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  padding: 12px;

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(120, 230, 255, 0.15);

    .list-title {
      margin: 0;
      color: rgba(220, 230, 240, 0.9);
      font-size: 18px;
      font-weight: 600;
      text-shadow: 0 0 4px rgba(120, 230, 255, 0.2);
    }

    .list-count {
      color: rgba(180, 200, 220, 0.7);
      font-size: 12px;
    }
  }

  .list-container {
    flex: 1;
    overflow: hidden;
    padding-right: 4px;

    .singer-item {
      margin-bottom: 10px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

// 滚动条样式
:deep(.el-scrollbar__bar) {
  opacity: 0.6;

  &.is-vertical {
    width: 6px;
  }

  &.is-horizontal {
    height: 6px;
  }
}

:deep(.el-scrollbar__thumb) {
  background-color: rgba(120, 230, 255, 0.3);
  border-radius: 3px;

  &:hover {
    background-color: rgba(120, 230, 255, 0.5);
  }
}
</style>
