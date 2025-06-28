<script lang="ts" setup>
import {computed, ref} from 'vue';
import type {SingerVO} from '@/music/types/vo/SingerVO';
import {CloseBold} from '@element-plus/icons-vue';
import {useSingerStore} from "@/music/stores";
import {ElMessage, type UploadFile} from "element-plus";
import {Plus} from '@element-plus/icons-vue';

const props = defineProps<{
  singer: SingerVO;
}>();

const singerStore = useSingerStore();

const showPictureDialog = ref(false);

const randomPicture = computed(() => {
  const pictureValues = Object.values(props.singer.pictureMap);
  if (pictureValues.length === 0) return '';

  const randomIndex = Math.floor(Math.random() * pictureValues.length);
  return pictureValues[randomIndex];
});

const deletePicture = async (index: number) => {
  const res = await singerStore.deleteSingerPicture(props.singer.id, index)
  if (res.code === 1) {
    ElMessage.success('图片已删除' as any)
  } else {
    ElMessage.error(res.message as any)
  }
};

const handlePictureChange = async (file: UploadFile) => {
  try {


    if (file === null) {
      ElMessage.warning("请选择新文件" as any);
      return;
    }

    const fileObjects = [file.raw as File];
    const res = await singerStore.uploadSingerPictures(props.singer.id, fileObjects);

    if (res.code === 1) {
      await singerStore.fetchSingerById(props.singer.id);
    } else {
      ElMessage.error(res.message as any);
    }
  } catch (error) {
    ElMessage.error("上传失败" as any);
    console.error(error);
  }
};

const showEditDialog = ref(false);  // 新增编辑对话框控制
const editForm = ref<SingerVO>({...props.singer});  // 编辑表单数据

// 初始化编辑表单
const initEditForm = () => {
  editForm.value = {...props.singer};
};

// 保存编辑
const saveSingerInfo = async () => {
  try {
    const res = await singerStore.updateSinger(editForm.value);
    if (res.code === 1) {
      ElMessage.success('歌手信息更新成功' as any);
      showEditDialog.value = false;
    } else {
      ElMessage.error(res.message || '更新失败' as any);
    }
  } catch (error) {
    ElMessage.error('更新过程中出错' as any);
    console.error(error);
  }
};
</script>

<template>
  <div class="singer-card">
    <el-avatar
        :size="120"
        :src="randomPicture"
        class="singer-avatar"
        @click="showPictureDialog = true"
    />
    <el-button
        @click="showEditDialog = true"
        class="singer-name">{{ singer.name }}
    </el-button>

    <!-- Picture Dialog -->
    <el-dialog
        v-model="showPictureDialog"
        width="80%"
        append-to-body
        destroy-on-close
    >
      <template #header="{ titleId, titleClass }">
        <div class="dialog-header">
          <span :id="titleId" :class="titleClass">{{ `${singer.name} ` }}</span>
          <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handlePictureChange"
              accept="image/*"
              multiple
          >
            <el-button
                type="primary"
                :icon="Plus"
                circle
                class="upload-btn"
            />
          </el-upload>
        </div>
      </template>

      <div class="picture-grid">
        <div
            v-for="([id, picture], index) in Object.entries(singer.pictureMap)"
            :key="index"
            class="picture-container"
        >
          <el-image
              :src="picture"
              fit="cover"
              :preview-src-list="Object.values(singer.pictureMap)"
              :initial-index="index"
              class="preview-image"
              :zoom-rate="1.2"
              :max-scale="7"
              :min-scale="0.2"
              :preview-teleported="true"
          />
          <el-button
              class="delete-btn"
              :icon="CloseBold"
              circle
              size="small"
              type="danger"
              @click.stop="deletePicture(Number(id))"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 编辑歌手信息对话框 -->
    <el-dialog
        v-model="showEditDialog"
        title="编辑歌手信息"
        width="30%"
        @closed="initEditForm"
        append-to-body
    >
      <el-form label-width="80px">
        <el-form-item label="歌手名称">
          <el-input v-model="editForm.name"/>
        </el-form-item>
        <!-- 可以添加更多编辑字段 -->
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveSingerInfo">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.singer-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: rgba(55, 55, 65, 0.6) !important;
  backdrop-filter: blur(10px) !important;
  border: 0.5px solid rgba(120, 230, 255, 0.15) !important;
  border-radius: 8px !important;
  box-shadow:
      0 0 10px rgba(120, 230, 255, 0.1),
      inset 0 0 8px rgba(120, 230, 255, 0.1) !important;
  transition: all 0.3s ease !important;
  width: fit-content;
  cursor: pointer;
}

.singer-card:hover {
  transform: translateY(-3px);
  box-shadow:
      0 0 15px rgba(120, 230, 255, 0.15),
      inset 0 0 10px rgba(120, 230, 255, 0.15) !important;
  background-color: rgba(55, 55, 65, 0.7) !important;
}

.singer-avatar {
  border: 1px solid rgba(120, 230, 255, 0.3) !important;
  box-shadow:
      0 0 8px rgba(120, 230, 255, 0.2),
      inset 0 0 5px rgba(120, 230, 255, 0.1) !important;
  transition: all 0.3s ease !important;
}

.singer-avatar:hover {
  transform: scale(1.05);
  box-shadow:
      0 0 12px rgba(120, 230, 255, 0.3),
      inset 0 0 8px rgba(120, 230, 255, 0.2) !important;
}

.singer-name {
  background-color: rgba(65, 65, 75, 0.7) !important;
  border: 0.5px solid rgba(120, 230, 255, 0.2) !important;
  color: rgba(220, 240, 255, 0.9) !important;
  border-radius: 20px !important;
  padding: 6px 16px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  text-shadow: 0 0 4px rgba(120, 230, 255, 0.3) !important;
  transition: all 0.3s ease !important;
}

.singer-name:hover {
  background-color: rgba(70, 70, 90, 0.8) !important;
  color: rgba(255, 255, 255, 1) !important;
  text-shadow: 0 0 6px rgba(120, 230, 255, 0.5) !important;
  transform: translateY(-1px);
}

.picture-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 8px;
}

.picture-container {
  position: relative;
  background-color: rgba(50, 50, 60, 0.5) !important;
  border-radius: 6px !important;
  overflow: hidden;
  transition: all 0.3s ease !important;
}

.picture-container:hover {
  transform: scale(1.02);
  box-shadow: 0 0 12px rgba(120, 230, 255, 0.2) !important;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: all 0.3s ease !important;
  background-color: rgba(200, 60, 60, 0.8) !important;
  border: none !important;
  backdrop-filter: blur(5px) !important;
}

.picture-container:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background-color: rgba(220, 80, 80, 0.9) !important;
  transform: scale(1.1);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: rgba(220, 240, 255, 0.9) !important;
}

.upload-btn {
  margin-left: 10px;
  background-color: rgba(70, 165, 255, 0.7) !important;
  border: 1px solid rgba(120, 230, 255, 0.3) !important;
}

/* 图片预览样式调整 */
:deep(.el-image-viewer__wrapper) {
  backdrop-filter: blur(20px) !important;
}

:deep(.el-image-viewer__mask) {
  background-color: rgba(20, 20, 30, 0.8) !important;
}

/* 图片预览全屏样式 */
:deep(.el-image-viewer) {
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-image-viewer__wrapper) {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

:deep(.el-image-viewer__mask) {
  opacity: 0.9 !important;
}

:deep(.el-image-viewer__img) {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
}

/* 图片容器样式调整 */
.preview-image {
  width: 100%;
  height: 150px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.preview-image:hover {
  transform: scale(1.03);
}
</style>
