<script setup lang="ts">
import { VideoPlay, Star, Search, Plus, StarFilled, VideoPause, Close } from "@element-plus/icons-vue";
import {useMusicStore, useCategoryStore, useMusicPlayStore, useSingerStore} from "@/music/stores";
import {onMounted, ref, watch} from "vue";
import { ElMessage } from "element-plus";
import type { UploadFile, UploadFiles } from 'element-plus';

const musicStore = useMusicStore();
const categoryStore = useCategoryStore();
const musicPlayStore = useMusicPlayStore();
const singerStore = useSingerStore();

const singerList = ref<Record<number, string>>([]);
const categoryList = ref<Record<number, string>>([]);

const formData = ref({
  title: '',
  singerId: null as number | null,
  categoryId: null as number | null,
  isFavorite: false,
});

// 批量上传相关状态
const showUploadDialog = ref(false);
const uploadFiles = ref<File[]>([]);
const uploadSinger = ref('');
const uploadCategory = ref('');

const handleSearch = async () => {
  musicStore.pageQuery = {
    ...musicStore.pageQuery,
    query: {
      title: formData.value.title,
      singerId: formData.value.singerId,
      categoryId: formData.value.categoryId,
      isFavorite: formData.value.isFavorite,
    }
  }
  await musicStore.fetchMusicPage()
}

const playMusicByRandom = () => {
  if (musicPlayStore.isPlaying === true) {
    musicPlayStore.pauseMusic();
  } else {
    musicStore.musics.length > 0 && musicPlayStore.playMusic(
        musicStore.musics[Math.floor(Math.random() * musicStore.musics.length)]
    );
  }
}

// 处理文件选择 - 修正后的类型定义
const handleFileChange = (
    uploadFile: UploadFile,
    _uploadFiles: UploadFiles
) => {
  if (uploadFile.raw && !uploadFiles.value.some(f => f.name === uploadFile.name)) {
    uploadFiles.value.push(uploadFile.raw);
  }
}

// 移除已选文件
const removeFile = (index: number) => {
  uploadFiles.value.splice(index, 1);
}

// 提交批量上传
const submitUpload = async () => {
  if (uploadFiles.value.length === 0) {
    ElMessage.warning('请至少选择一个音乐文件' as any);
    return;
  }

  try {
    await musicStore.createMusics(uploadFiles.value, uploadSinger.value, uploadCategory.value);
    ElMessage.success('批量上传成功' as  any);
    showUploadDialog.value = false;
    uploadFiles.value = [];
    uploadSinger.value = '';
    uploadCategory.value = '';
    await handleSearch(); // 刷新列表
  } catch (error) {
    console.error('批量上传失败:', error);
    ElMessage.error('批量上传失败' as any);
  }
}

watch(
    () => [
      formData.value.title,
      formData.value.singerId,
      formData.value.categoryId,
      formData.value.isFavorite
    ],
    handleSearch
);

onMounted(async () => {
  const res01 = await singerStore.fetchSingerList()
  const res02 = await categoryStore.fetchCategoryList()
  if (res01.code === 1) {
    singerList.value = res01.data
  }
  if (res02.code === 1) {
    categoryList.value = res02.data
  }
})
</script>

<template>
  <div class="song-header">
    <!-- 左侧：大尺寸歌曲封面图片 -->
    <el-image
        class="song-cover"
        :src="musicPlayStore.currentMusic?.pictureUrl"
        fit="cover"
    />

    <!-- 右侧：歌曲信息和操作区域 -->
    <div class="right-section">
      <!-- 第一行：大字号歌手名称 -->
      <div class="artist-info">
        <h1 class="artist-name">{{ musicPlayStore.currentMusic?.singerName || '佚名' }}</h1>
      </div>

      <!-- 第二行：操作按钮组（底部对齐图片） -->
      <div class="action-buttons">
        <!-- 播放按钮 -->
        <el-button
            class="play-btn"
            :type="musicPlayStore.isPlaying ? 'success' : 'primary'"
            @click="playMusicByRandom"
            circle>
          <el-icon size="20">
            <VideoPause v-if="musicPlayStore.isPlaying" />
            <VideoPlay v-else />
          </el-icon>
        </el-button>

        <!-- 搜索框 -->
        <el-input
            v-model="formData.title"
            placeholder="搜索歌曲..."
            class="song-search"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <!-- 分类下拉框 -->
        <el-select
            v-model="formData.categoryId"
            placeholder="分类"
            class="category-select"
            clearable
            filterable
        >
          <el-option
              v-for="([id, name], index) in Object.entries(categoryList)"
              :key="index"
              :label="name"
              :value="Number(id)"
          />
        </el-select>

        <!-- 添加按钮 -->
        <el-button
            type="primary"
            class="add-song-btn"
            @click="showUploadDialog = true"
        >
          <el-icon><Plus /></el-icon>
          <span>添加歌曲</span>
        </el-button>

        <!-- 收藏按钮 -->
        <el-button class="favorite-btn" @click="formData.isFavorite = !formData.isFavorite">
          <el-icon>
            <StarFilled v-if="formData.isFavorite" />
            <Star v-else />
          </el-icon>
          <span>收藏</span>
        </el-button>
      </div>
    </div>
  </div>

  <!-- 批量上传对话框 -->
  <el-dialog
      v-model="showUploadDialog"
      title="批量添加歌曲"
      width="600px"
      :close-on-click-modal="false"
  >
    <el-form label-width="80px">
      <el-form-item label="歌手">
        <el-select
            v-model="uploadSinger"
            placeholder="请选择歌手"
            filterable
            clearable
            style="width: 100%"
        >
          <el-option
              v-for="([_id, name], index) in Object.entries(singerList)"
              :key="index"
              :label="name"
              :value="name"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="分类">
        <el-select
            v-model="uploadCategory"
            placeholder="请选择分类"
            filterable
            clearable
            style="width: 100%"
        >
          <el-option
              v-for="([_id, name], index) in Object.entries(categoryList)"
              :key="index"
              :label="name"
              :value="name"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="音乐文件">
        <el-upload
            multiple
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="false"
            accept="audio/*"
        >
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">支持批量上传音频文件</div>
          </template>
        </el-upload>

        <div class="file-list" v-if="uploadFiles.length > 0">
          <div v-for="(file, index) in uploadFiles" :key="index" class="file-item">
            <span>{{ file.name }}</span>
            <el-icon @click="removeFile(index)"><Close /></el-icon>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="showUploadDialog = false">取消</el-button>
      <el-button type="primary" @click="submitUpload" :loading="musicStore.loading">
        上传
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.song-header {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  padding: 16px;
  height: 140px;
}

.song-cover {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.right-section {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  flex-grow: 1;
}

.artist-info {
  margin-bottom: auto;
}

.artist-name {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin: 0;
  line-height: 1.2;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-top: 10px;
  flex-wrap: wrap;
}

.play-btn {
  width: 44px;
  height: 44px;
}

.song-search {
  flex: 1;
  min-width: 200px;
  max-width: 300px;
}

.category-select {
  width: 120px;
}

/* 文件列表样式 */
.file-list {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 8px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  margin-bottom: 4px;
  background-color: var(--el-fill-color-light);
  border-radius: 3px;
}

.file-item:hover {
  background-color: var(--el-fill-color);
}

.file-item .el-icon {
  cursor: pointer;
  color: var(--el-color-danger);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .song-header {
    flex-direction: column;
    height: auto;
    align-items: flex-start;
  }

  .right-section {
    width: 100%;
  }

  .action-buttons {
    flex-wrap: wrap;
  }

  .song-search,
  .category-select {
    min-width: 100%;
  }
}
</style>
