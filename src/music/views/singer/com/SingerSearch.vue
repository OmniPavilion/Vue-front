<script setup lang="ts">
import { ref, watch, onBeforeUnmount } from 'vue';
import { Search, Plus, CloseBold } from '@element-plus/icons-vue';
import { useSingerStore } from '@/music/stores';
import type { SingerVO } from '@/music/types/vo/SingerVO';
import { ElMessage } from 'element-plus';

const singerStore = useSingerStore();

// 添加歌手对话框相关状态
const showAddDialog = ref(false);
const newSinger = ref<SingerVO>({
  id: 0,
  name: '',
  pictureMap: {}
});

// 图片上传相关状态
const uploadFiles = ref<{file: File, url: string}[]>([]);

// 监听搜索条件变化
watch(
    () => singerStore.pageQuery.query,
    () => {
      singerStore.pageQuery.pageNum = 1;
      handleSearch();
    }
);

// 搜索方法
const handleSearch = async () => {
  await singerStore.fetchSingerPage();
};

// 打开添加歌手对话框
const openAddDialog = () => {
  newSinger.value = {
    id: 0,
    name: '',
    pictureMap: {}
  };
  clearUploadFiles();
  showAddDialog.value = true;
};

// 处理图片上传
const handlePictureUpload = (uploadFile: { raw: File }) => {
  if (uploadFile?.raw) {
    const url = URL.createObjectURL(uploadFile.raw);
    uploadFiles.value.push({
      file: uploadFile.raw,
      url
    });
  }
  return false; // 阻止自动上传
};

// 移除已选择的图片
const removePicture = (index: number) => {
  URL.revokeObjectURL(uploadFiles.value[index].url);
  uploadFiles.value.splice(index, 1);
};

// 清空上传的文件
const clearUploadFiles = () => {
  uploadFiles.value.forEach(file => {
    URL.revokeObjectURL(file.url);
  });
  uploadFiles.value = [];
};

// 提交添加歌手表单
const submitAddSinger = async () => {
  if (!newSinger.value.name) {
    ElMessage.warning('请输入歌手姓名' as any);
    return;
  }

  try {
    const createRes = await singerStore.createSinger(newSinger.value);

    if (createRes.code === 1 && uploadFiles.value.length > 0) {
      const singerId = createRes.data;
      if (singerId) {
        const files = uploadFiles.value.map(item => item.file);
        await singerStore.uploadSingerPictures(singerId, files);
      }
    } else {
      ElMessage.warning(createRes.message as any);
      return;
    }

    ElMessage.success('添加歌手成功' as any);
    showAddDialog.value = false;
    await handleSearch();
  } catch (error) {
    ElMessage.error('添加歌手失败' as any);
    console.error(error);
  } finally {
    clearUploadFiles();
  }
};

onBeforeUnmount(() => {
  clearUploadFiles();
});

handleSearch();
</script>

<template>
  <div class="singer-search-container">
    <!-- 搜索栏 -->
    <div class="search-controls">
      <div class="search-input-container">
        <el-input
            v-model="singerStore.pageQuery.query"
            placeholder="搜索歌手名称..."
            clearable
            @change="handleSearch"
            @clear="handleSearch"
            class="search-input"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <el-button
          type="primary"
          @click="openAddDialog"
          :icon="Plus"
          class="add-button"
      >
        添加歌手
      </el-button>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
          v-model:current-page="singerStore.pageQuery.pageNum"
          v-model:page-size="singerStore.pageQuery.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="singerStore.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
          class="pagination"
      />
    </div>

    <!-- 添加歌手对话框 -->
    <el-dialog
        v-model="showAddDialog"
        title="添加新歌手"
        width="600px"
        @closed="clearUploadFiles"
        class="add-singer-dialog"
        append-to-body
    >
      <el-form :model="newSinger" label-width="100px">
        <el-form-item label="歌手名称" required>
          <el-input
              v-model="newSinger.name"
              placeholder="请输入歌手名称"
              class="name-input"
          />
        </el-form-item>

        <el-form-item label="歌手图片">
          <div class="upload-section">
            <el-upload
                multiple
                :auto-upload="false"
                :on-change="handlePictureUpload"
                :show-file-list="false"
                accept="image/*"
                list-type="picture-card"
                class="avatar-uploader"
            >
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-tip">点击上传图片</div>
            </el-upload>

            <div class="upload-tips">
              <div class="tip-text">支持 JPG/PNG 格式</div>
              <div class="tip-text">图片大小不超过 25MB</div>
            </div>
          </div>
          <div>111</div>

          <div class="preview-container" v-if="uploadFiles.length > 0">
            <div class="preview-title">已上传图片:</div>
            <div class="preview-grid">
              <div
                  class="preview-item"
                  v-for="(item, index) in uploadFiles"
                  :key="index"
              >
                <el-image
                    :src="item.url"
                    :preview-src-list="uploadFiles.map(f => f.url)"
                    :initial-index="index"
                    fit="cover"
                    class="preview-image"
                    hide-on-click-modal
                >
                  <template #error>
                    <div class="image-error">图片加载失败</div>
                  </template>
                </el-image>
                <el-button
                    circle
                    size="small"
                    type="danger"
                    @click.stop="removePicture(index)"
                    class="delete-btn"
                >
                  <el-icon><CloseBold /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddDialog = false" class="cancel-btn">取消</el-button>
          <el-button
              type="primary"
              @click="submitAddSinger"
              class="confirm-btn"
          >
            确认添加
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.singer-search-container {
  padding: 20px;
  background-color: rgba(55, 55, 65, 0.6);
  border-radius: 8px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(120, 230, 255, 0.1);
  box-shadow: 0 0 15px rgba(120, 230, 255, 0.05);
}

.search-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input-container {
  flex: 1;
}

.search-input {
  :deep(.el-input__wrapper) {
    background-color: rgba(65, 65, 75, 0.7);
    border: 1px solid rgba(120, 230, 255, 0.2);
    box-shadow: 0 0 8px rgba(120, 230, 255, 0.1);

    &:hover {
      border-color: rgba(120, 230, 255, 0.3);
    }

    &.is-focus {
      border-color: rgba(120, 230, 255, 0.4);
      box-shadow: 0 0 0 1px rgba(120, 230, 255, 0.3);
    }
  }
}

.search-icon {
  color: rgba(120, 230, 255, 0.7);
  font-size: 16px;
}

.add-button {
  background-color: rgba(70, 165, 255, 0.8);
  border: 1px solid rgba(120, 230, 255, 0.3);
  transition: all 0.3s ease;

  &:hover {
    background-color: rgba(70, 165, 255, 0.9);
    transform: translateY(-1px);
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.add-singer-dialog {
  :deep(.el-dialog) {
    background-color: rgba(60, 60, 70, 0.9);
    border: 1px solid rgba(120, 230, 255, 0.15);
    border-radius: 8px;
  }

  :deep(.el-dialog__header) {
    border-bottom: 1px solid rgba(120, 230, 255, 0.1);
    margin-right: 0;
  }

  :deep(.el-dialog__title) {
    color: rgba(220, 240, 255, 0.9);
    font-size: 18px;
  }
}

.upload-section {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 16px;
}

.avatar-uploader {
  :deep(.el-upload) {
    width: 120px;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: rgba(70, 70, 80, 0.5);
    border: 1px dashed rgba(120, 230, 255, 0.3);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(120, 230, 255, 0.5);
      background-color: rgba(70, 70, 80, 0.7);
    }
  }
}

.upload-icon {
  font-size: 24px;
  color: rgba(120, 230, 255, 0.7);
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 12px;
  color: rgba(180, 200, 220, 0.8);
}

.upload-tips {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tip-text {
  font-size: 12px;
  color: rgba(160, 180, 200, 0.7);
}

.preview-container {
  margin-top: 16px;
}

.preview-title {
  color: rgba(180, 200, 220, 0.9);
  font-size: 14px;
  margin-bottom: 12px;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.preview-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(120, 230, 255, 0.2);
  }
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 6px;
  background-color: rgba(200, 60, 60, 0.8) !important;
  border: none !important;

  &:hover {
    background-color: rgba(220, 80, 80, 0.9) !important;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  background-color: rgba(70, 70, 80, 0.6);
  border: 1px solid rgba(120, 120, 140, 0.3);
  color: rgba(220, 230, 240, 0.9);

  &:hover {
    background-color: rgba(80, 80, 90, 0.7);
  }
}

.confirm-btn {
  background-color: rgba(70, 165, 255, 0.8);
  border: 1px solid rgba(120, 230, 255, 0.3);

  &:hover {
    background-color: rgba(70, 165, 255, 0.9);
  }
}

@media (max-width: 768px) {
  .search-controls {
    flex-direction: column;
    gap: 12px;
  }

  .search-input-container {
    width: 100%;
  }

  .add-button {
    width: 100%;
  }

  .upload-section {
    flex-direction: column;
    gap: 12px;
  }

  .preview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
