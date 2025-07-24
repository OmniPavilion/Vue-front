<script setup lang="ts">
import { Picture } from '@element-plus/icons-vue'
import type { SingerVO } from '@/music/types/vo/SingerVO'
import {useMusicStore, useSingerStore} from "@/music/stores";
import {computed} from 'vue'

const singerStore = useSingerStore()
const musicStore = useMusicStore()

const props = defineProps<{
  singer: SingerVO
}>()

const isActive = computed(() => {
  return props.singer.id === singerStore.currentSinger?.id
})

// 获取图片URL，优先使用小尺寸图片
const getImageUrl = (pictureMap: Record<number, string>) => {
  return pictureMap[1] || pictureMap[2] || Object.values(pictureMap)[0] || ''
}

const handleClick = () => {
  singerStore.currentSinger = props.singer
  musicStore.pageQuery.query = {
    ...musicStore.pageQuery.query,
    singerId: props.singer.id
  }
  musicStore.fetchMusicPage()
}
</script>

<template>
  <div
      class="singer-side-card"
      @click="handleClick"
      :class="{ 'active': isActive }"
  >
    <div class="singer-content">
      <div class="singer-image-container">
        <el-image
            :src="getImageUrl(props.singer.pictureMap)"
            fit="cover"
            class="singer-image"
            :alt="singer.name || '全部'"
            loading="lazy"
        >
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
          <template #placeholder>
            <div class="image-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
      </div>
      <div class="singer-info">
        <h3 class="singer-name">{{ singer.name || '全部'}}</h3>
      </div>
    </div>
  </div>
</template>



<style scoped lang="scss">
.singer-side-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border-radius: 8px;
  overflow: hidden;
  margin-right: 12px;
  margin-left: 6px;


  &:hover {
    transform: translateX(4px);
    box-shadow:
        0 0 14px rgba(120, 230, 255, 0.15),
        inset 0 0 12px rgba(120, 230, 255, 0.12);
    border-color: rgba(120, 230, 255, 0.25);

    .singer-name {
      color: rgba(120, 230, 255, 0.9);
      text-shadow: 0 0 6px rgba(120, 230, 255, 0.35);
    }

    .singer-image {
      transform: scale(1.05);
    }
  }

  &.active {
    background-color: rgba(70, 165, 255, 0.25);
    border-left: 3px solid rgba(120, 230, 255, 0.6);
    box-shadow:
        0 0 15px rgba(70, 165, 255, 0.25),
        inset 0 0 10px rgba(120, 230, 255, 0.15);
  }
}

.singer-content {
  display: flex;
  align-items: center;
  padding: 2px 6px;
}

.singer-image-container {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  border-radius: 10%;
  overflow: hidden;
  margin-right: 16px;
  border: 0.5px solid rgba(120, 230, 255, 0.2);
  box-shadow:
      0 0 8px rgba(120, 230, 255, 0.1),
      inset 0 0 6px rgba(120, 230, 255, 0.05);
}

.singer-image {
  width: 100%;
  height: 100%;
  display: block;
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.image-error,
.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(45, 45, 55, 0.5);
  color: rgba(120, 230, 255, 0.5);

  .el-icon {
    font-size: 24px;
  }
}

.singer-info {
  flex-grow: 1;
  min-width: 0;
}

.singer-name {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: rgba(220, 230, 240, 0.9);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.3s ease;
}

.singer-id {
  font-size: 12px;
  color: rgba(180, 200, 220, 0.7);
  display: block;
  margin-top: 4px;
}
</style>
