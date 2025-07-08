<script setup lang="ts">
import {VideoPlay, Star, Search, Plus, StarFilled, VideoPause, Close} from "@element-plus/icons-vue";
import {useMusicStore, useCategoryStore, useMusicPlayStore, useSingerStore} from "@/music/stores";
import {onMounted, ref, watch, computed} from "vue";
import type {UploadFile, UploadFiles} from 'element-plus';
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete} from "@element-plus/icons-vue";

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
    musicPlayStore.playRandom();
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
    ElMessage.success('批量上传成功' as any);
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

// 批量删除方法
const batchDelete = async () => {
  if (musicStore.deleteMusicIds.length === 0) {
    ElMessage.warning('请至少选择一首歌曲' as any);
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${musicStore.deleteMusicIds.length} 首歌曲吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 禁止删除正在播放的文件
    if (musicPlayStore.currentMusic && musicStore.deleteMusicIds.includes(musicPlayStore.currentMusic.id)) {
      ElMessage.error('禁止删除正在播放的文件' as any);
      return;
    }
    const res = await musicStore.deleteMusics(musicStore.deleteMusicIds);
    if (res.code !== 1) {
      ElMessage.error(res.message as any);
      return;
    }
    ElMessage.success(`成功删除 ${musicStore.deleteMusicIds.length} 首歌曲` as any);
    musicStore.deleteMusicIds = [];
    musicStore.isDeleteMode = false;
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败' as any);
    }
  }
};

// 切换批量模式
const toggleBatchMode = () => {
  musicStore.isDeleteMode = !musicStore.isDeleteMode;
  if (!musicStore.isDeleteMode) {
    musicStore.deleteMusicIds = [];
  }
};

// 计算总文件大小
const totalFileSize = computed(() => {
  return uploadFiles.value.reduce((total, file) => total + file.size, 0)
})

// 格式化文件名（省略过长的部分）
const formatFileName = (name: any) => {
  if (name.length > 30) {
    return `${name.substring(0, 15)}...${name.substring(name.length - 10)}`
  }
  return name
}

// 格式化文件大小
const formatFileSize = (bytes: any) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
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
        <h2 class="title-name">{{ musicPlayStore.currentMusic?.title }}</h2>
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
            <VideoPause v-if="musicPlayStore.isPlaying"/>
            <VideoPlay v-else/>
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
            <el-icon>
              <Search/>
            </el-icon>
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
          <el-icon>
            <Plus/>
          </el-icon>
          <span>添加歌曲</span>
        </el-button>

        <!-- 收藏按钮 -->
        <el-button class="favorite-btn" @click="formData.isFavorite = !formData.isFavorite">
          <el-icon>
            <StarFilled v-if="formData.isFavorite"/>
            <Star v-else/>
          </el-icon>
          <span>收藏</span>
        </el-button>

        <el-button
            type="danger"
            @click="toggleBatchMode"
            :class="{ 'active-batch': musicStore.isDeleteMode }"
        >
          <el-icon>
            <Delete/>
          </el-icon>
          <span>{{ musicStore.isDeleteMode ? '取消批量' : '批量删除' }}</span>
        </el-button>

        <el-button
            v-if="musicStore.isDeleteMode"
            type="danger"
            @click="batchDelete"
            :disabled="musicStore.deleteMusicIds.length === 0"
        >
          <el-icon>
            <Delete/>
          </el-icon>
          <span>删除选中({{ musicStore.deleteMusicIds.length }})</span>
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
      append-to-body
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
      </el-form-item>

      <el-form-item>
        <!-- 优化后的文件列表 -->
        <div class="enhanced-file-list" v-if="uploadFiles.length > 0">
          <div class="file-list-header">
            <span class="file-name">文件名</span>
            <span class="file-size">大小</span>
            <span class="file-actions">操作</span>
          </div>
          <div class="file-list-body">
            <div v-for="(file, index) in uploadFiles" :key="index" class="file-item">
              <span class="file-name" :title="file.name">{{ formatFileName(file.name) }}</span>
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
              <span class="file-actions">
                <el-tooltip effect="dark" content="移除" placement="top">
                  <el-icon @click="removeFile(index)"><Close/></el-icon>
                </el-tooltip>
              </span>
            </div>
          </div>
          <div class="file-list-footer">
            共 {{ uploadFiles.length }} 个文件，总计 {{ formatFileSize(totalFileSize) }}
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
  box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.2),
      0 0 8px rgba(120, 230, 255, 0.2);
  border: 0.5px solid rgba(120, 230, 255, 0.2);
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.02);
    box-shadow:
        0 6px 16px rgba(0, 0, 0, 0.3),
        0 0 12px rgba(120, 230, 255, 0.3);
  }
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
  color: rgba(220, 230, 240, 0.95);
  margin: 0;
  line-height: 1.2;
  text-shadow: 0 0 6px rgba(120, 230, 255, 0.3);
}

.title-name {
  font-size: 18px;
  font-weight: normal;
  color: rgba(200, 220, 240, 0.85);
  margin: 4px 0 0 0;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
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
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 4px 16px rgba(70, 130, 255, 0.4);
  }
}

.song-search {
  flex: 1;
  min-width: 100px;
  max-width: 500px;
}

.category-select {
  width: 100px;
}

:deep(.el-dialog) {
  .el-dialog__header {
    border-bottom: 0.5px solid rgba(120, 230, 255, 0.1);
  }

  .el-dialog__body {
    padding: 20px;
  }

  .el-form-item__label {
    color: rgba(200, 220, 240, 0.9);
  }
}

.enhanced-file-list {
  margin-top: 12px;
  border: 0.5px solid rgba(120, 230, 255, 0.2);
  border-radius: 4px;
  overflow: hidden;
  background-color: rgba(65, 65, 75, 0.6);
  box-shadow: inset 0 0 8px rgba(120, 230, 255, 0.05);
}

.file-list-header {
  display: flex;
  padding: 8px 12px;
  background-color: rgba(70, 70, 80, 0.7);
  font-weight: 500;
  color: rgba(180, 220, 255, 0.9);
  border-bottom: 0.5px solid rgba(120, 230, 255, 0.15);
}

.file-list-body {
  max-height: 300px;
  overflow-y: auto;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 0.5px solid rgba(120, 230, 255, 0.1);
  transition: all 0.2s ease;

  &:hover {
    background-color: rgba(120, 230, 255, 0.1);
  }
}

.file-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 12px;
  color: rgba(220, 230, 240, 0.9);
}

.file-size {
  width: 80px;
  text-align: right;
  padding-right: 12px;
  color: rgba(180, 200, 220, 0.8);
  font-size: 0.9em;
}

.file-actions .el-icon {
  cursor: pointer;
  color: rgba(255, 120, 120, 0.8);
  transition: all 0.2s ease;

  &:hover {
    color: rgba(255, 120, 120, 1);
    transform: scale(1.2);
  }
}

.file-list-footer {
  padding: 8px 12px;
  background-color: rgba(70, 70, 80, 0.7);
  text-align: center;
  font-size: 0.9em;
  color: rgba(180, 200, 220, 0.8);
  border-top: 0.5px solid rgba(120, 230, 255, 0.1);
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

  .file-name {
    max-width: 180px;
  }

  .file-size {
    width: 60px;
  }
}
</style>
