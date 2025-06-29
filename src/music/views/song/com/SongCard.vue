<script setup lang="ts">
import type { MusicVO } from '@/music/types/vo/MusicVO';
import {useCategoryStore, useMusicStore, useSingerStore, useMusicPlayStore} from "@/music/stores";
import { VideoPause, VideoPlay, StarFilled, Star, Edit } from "@element-plus/icons-vue";
import { ref, computed } from 'vue';
import {ElMessage} from "element-plus";

const props = defineProps<{
  music: MusicVO;
}>();

const musicStore = useMusicStore();
const singerStore = useSingerStore();
const categoryStore = useCategoryStore();
const musicPlayStore = useMusicPlayStore();

const showEditDialog = ref(false);
const showActions = ref(false);

const singerList = ref<Record<number, string>>([])
const categoryList = ref<Record<number, string>>([])

const shouldShowActions = computed(() => {
  return showActions.value || isPlay.value;
});

const isPlay = computed(() => {
  return props.music.id === musicPlayStore.currentMusic?.id && musicPlayStore.isPlaying;
});

const loadSelectData = async () => {
  try {
    const res01 = await singerStore.fetchSingerList();
    const res02 = await categoryStore.fetchCategoryList();

    singerList.value = res01.data;
    categoryList.value = res02.data;
  } catch (error) {
    console.error('加载选择数据失败:', error);
  }
};

const toggleFavorite = () => {
  musicStore.toggleFavorite(props.music.id);
};

const togglePlay = () => {
  if (musicPlayStore.isPlaying === true &&
      musicPlayStore.currentMusic?.id === props.music.id) {
    musicPlayStore.pauseMusic();
  } else {
    musicPlayStore.playMusic(props.music);
  }
};

const openEditDialog = async () => {
  await loadSelectData();
  showEditDialog.value = true;
};

const submitEdit = async () => {
  try {
    const res = await musicStore.updateMusic(props.music);
    if (res.code === -1) {
      ElMessage.error(res.message as any)
      return
    } else {
      ElMessage.success('歌曲已更新' as any)
    }
    showEditDialog.value = false;
  } catch (error) {
    console.error('更新歌曲失败:', error);
  }
};

const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};
</script>

<template>
  <div
      class="song-item"
      :class="{ 'is-playing': isPlay }"
      @mouseenter="showActions = true"
      @mouseleave="showActions = false"
  >
    <div class="song-info">
      <span class="song-title">{{ music.title }}</span>
      <span class="song-singer">{{ music.singerName || '--' }}</span>
      <span class="song-category">{{ music.categoryName || '--' }}</span>
      <span class="song-duration">{{ formatDuration(music.duration) }}</span>
    </div>

    <div class="song-actions" :class="{ 'show-actions': shouldShowActions }">
      <el-button
          :type="isPlay ? 'success' : 'primary'"
          circle
          size="small"
          @click="togglePlay"
          class="action-btn"
      >
        <el-icon>
          <VideoPause v-if="isPlay"></VideoPause>
          <VideoPlay v-else></VideoPlay>
        </el-icon>
      </el-button>
      <el-button
          type="warning"
          circle
          size="small"
          @click="openEditDialog"
          class="action-btn"
      >
        <el-icon>
          <Edit></Edit>
        </el-icon>
      </el-button>
      <el-button
          :type="music.isFavorite ? 'danger' : 'info'"
          circle
          size="small"
          @click="toggleFavorite"
          class="action-btn"
      >
        <el-icon>
          <StarFilled v-if="music.isFavorite"></StarFilled>
          <Star v-else></Star>
        </el-icon>
      </el-button>
    </div>
  </div>

  <!-- 编辑对话框 -->
  <el-dialog
      v-model="showEditDialog"
      title="编辑歌曲信息"
      width="500px"
      :close-on-click-modal="false"
      class="neon-dialog"
  >
    <el-form :model="props.music" label-width="80px">
      <el-form-item label="歌曲名称">
        <el-input v-model="props.music.title" />
      </el-form-item>
      <el-form-item label="歌手">
        <el-select
            v-model="props.music.singerName"
            filterable
            clearable
            placeholder="请选择歌手"
            style="width: 100%"
        >
          <el-option
              v-for="(singer, index) in singerList"
              :key="index"
              :label="singer"
              :value="singer"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="分类">
        <el-select
            v-model="props.music.categoryName"
            filterable
            clearable
            placeholder="请选择分类"
        >
          <el-option
              v-for="(category, index) in categoryList"
              :key="index"
              :label="category"
              :value="category"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.song-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  margin: 6px 0;
  border-radius: 6px;
  background-color: rgba(60, 60, 70, 0.6);
  backdrop-filter: blur(8px);
  border: 0.5px solid rgba(120, 230, 255, 0.15);
  box-shadow:
      0 2px 8px rgba(0, 0, 0, 0.1),
      inset 0 0 6px rgba(120, 230, 255, 0.05);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;

  &:hover {
    background-color: rgba(70, 70, 80, 0.7);
    box-shadow:
        0 4px 12px rgba(0, 0, 0, 0.15),
        inset 0 0 8px rgba(120, 230, 255, 0.1);
    transform: translateY(-1px);
  }

  &.is-playing {
    background-color: rgba(70, 165, 255, 0.2);
    border-color: rgba(120, 230, 255, 0.3);
    box-shadow:
        0 0 15px rgba(70, 165, 255, 0.2),
        inset 0 0 10px rgba(120, 230, 255, 0.1);

    .song-title {
      color: rgba(220, 240, 255, 0.95);
      text-shadow: 0 0 6px rgba(120, 230, 255, 0.4);
    }
  }
}

.song-info {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
  gap: 16px;
}

.song-title {
  width: 30%;
  min-width: 150px;
  font-weight: 500;
  color: rgba(220, 230, 240, 0.9);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.song-singer {
  width: 25%;
  min-width: 120px;
  color: rgba(180, 200, 220, 0.8);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.song-category {
  width: 20%;
  min-width: 100px;
  color: rgba(180, 200, 220, 0.8);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.song-duration {
  width: 15%;
  min-width: 60px;
  color: rgba(160, 180, 200, 0.7);
  text-align: right;
}

.song-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;

  &.show-actions {
    opacity: 1;
    transform: translateX(0);
  }
}

.action-btn {
  transition: all 0.3s ease !important;

  &:hover {
    transform: translateY(-2px) scale(1.05) !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2) !important;
  }

  &:active {
    transform: translateY(0) scale(0.98) !important;
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .song-item {
    flex-wrap: wrap;
    padding: 10px;
  }

  .song-info {
    width: 100%;
    margin-bottom: 8px;
    flex-wrap: wrap;
    gap: 8px;
  }

  .song-title,
  .song-singer,
  .song-category,
  .song-duration {
    width: auto;
    min-width: unset;
    margin-right: 8px;
  }

  .song-actions {
    width: 100%;
    justify-content: flex-end;
    margin-left: 0;
    opacity: 1;
    transform: none;
  }
}
</style>
