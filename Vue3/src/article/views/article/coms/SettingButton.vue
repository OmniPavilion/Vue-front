<script setup>
import {ref, onMounted} from 'vue'
import {Setting, Edit, Check, Close, RefreshRight, DocumentChecked} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {useArticleFileStore} from '@/article/stores'

const articleFileStore = useArticleFileStore()
const drawerVisible = ref(false)
const direction = ref('rtl')
const fileInput = ref(null)
const isEditing = ref(false)
const tempPath = ref('')

onMounted(async () => {
  await loadCurrentPath()
})

const loadCurrentPath = async () => {
  try {
    const res = await articleFileStore.getRoot()
    if (res.code === 1) {
      tempPath.value = res.data
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('获取目录失败: ' + error.message)
  }
}

const openSettings = async () => {
  await loadCurrentPath()
  drawerVisible.value = true
}

const openFilePicker = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = event.target.files
  if (files.length > 0) {
    const path = files[0].webkitRelativePath.split('/')[0]
    articleFileStore.rootPath = path
    event.target.value = ''
  }
}

const startEditing = () => {
  tempPath.value = articleFileStore.rootPath
  isEditing.value = true
}

const saveEditing = async () => {
  isEditing.value = false
  await saveSettings()
}

const cancelEditing = () => {
  articleFileStore.rootPath = tempPath.value
  isEditing.value = false
}

const saveSettings = async () => {
  try {
    if (!articleFileStore.rootPath) {
      ElMessage.warning('请先选择Markdown文件目录')
      return
    }
    await articleFileStore.updateRoot(articleFileStore.rootPath)
    ElMessage.success('目录设置成功')
  } catch (error) {
    ElMessage.error('保存目录失败: ' + error.message)
  }
}

const resetRootPath = async () => {
  try {
    await articleFileStore.resetRoot()
    await loadCurrentPath()
    ElMessage.success('目录已重置为默认值')
  } catch (error) {
    ElMessage.error('重置目录失败: ' + error.message)
  }
}

const copyFile = async () => {
  try {
    await articleFileStore.downloadAll()
    ElMessage.success('文件复制成功')
  } catch (error) {
    ElMessage.error('文件复制失败: ' + error.message)
  }
}
</script>

<template>
  <div>
    <el-button
        class="setting-btn"
        :icon="Setting"
        circle
        @click="openSettings"
    />

    <el-drawer
        v-model="drawerVisible"
        title="设置"
        :direction="direction"
        size="360px"
        append-to-body
        class="custom-drawer"
    >
      <div class="setting-content">
        <div class="setting-item">
          <div class="input-container">
            <el-input
                v-model="articleFileStore.rootPath"
                placeholder="请输入Markdown文件目录路径"
                :readonly="!isEditing"
                class="path-input"
            />
            <div class="action-buttons">
              <el-button-group>
                <el-button
                    v-if="!isEditing"
                    @click="startEditing"
                    class="action-btn"
                    title="编辑"
                >
                  <el-icon>
                    <Edit/>
                  </el-icon>
                </el-button>
                <template v-else>
                  <el-button
                      @click="saveEditing"
                      type="success"
                      class="action-btn"
                      title="保存"
                  >
                    <el-icon>
                      <Check/>
                    </el-icon>
                  </el-button>
                  <el-button
                      @click="cancelEditing"
                      class="action-btn"
                      title="取消"
                  >
                    <el-icon>
                      <Close/>
                    </el-icon>
                  </el-button>
                </template>
                <el-button
                    @click="resetRootPath"
                    :disabled="!articleFileStore.rootPath"
                    class="action-btn"
                    title="重置"
                >
                  <el-icon>
                    <RefreshRight/>
                  </el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>

          <div class="check-file-container">
            <el-tooltip effect="dark" content="备份文件" placement="top">
              <el-button
                  @click="copyFile"
                  class="check-file-btn"
                  :icon="DocumentChecked"
                  circle
              />
            </el-tooltip>
          </div>
        </div>
      </div>
    </el-drawer>

    <input
        type="file"
        ref="fileInput"
        style="display: none"
        @change="handleFileSelect"
        webkitdirectory
    />
  </div>
</template>

<style scoped lang="scss">
.setting-button-container {
  display: inline-block;

  .setting-btn {
    margin: 5px;
    font-size: 18px;
    transition: all 0.3s ease;
    background-color: var(--el-color-primary-light-9);

    &:hover {
      transform: scale(1.1);
      background-color: var(--el-color-primary-light-7);
    }
  }
}

.custom-drawer {
  .el-drawer__header {
    margin-bottom: 16px;
    padding: 20px 20px 0;
    color: var(--el-text-color-primary);
    font-weight: 600;
    font-size: 18px;
  }

  .el-drawer__body {
    padding: 0 20px 20px;
  }
}

.setting-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0 8px;
}

.setting-item {
  flex: 1;

  .input-container {
    display: flex;
    gap: 8px;
    align-items: center;

    .path-input {
      flex: 1;

      :deep(.el-input__inner) {
        padding-right: 40px;
      }
    }

    .action-buttons {
      display: flex;

      .el-button-group {
        display: flex;
        gap: 4px;

        .action-btn {
          padding: 8px;
          transition: all 0.2s ease;

          &:hover {
            transform: translateY(-1px);
          }
        }
      }
    }
  }
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid var(--el-border-color-light);

  .save-btn {
    min-width: 100px;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }
}

.check-file-container {
  margin-top: 16px;
  text-align: center;

  .check-file-btn {
    width: 40px;
    height: 40px;
    font-size: 18px;
    background: rgba(65, 65, 75, 0.6);
    border: 0.5px solid rgba(120, 230, 255, 0.2);
    color: rgba(180, 220, 255, 0.9);
    transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);

    &:hover {
      background: rgba(70, 70, 80, 0.7);
      border-color: rgba(120, 230, 255, 0.4);
      color: rgba(120, 230, 255, 1);
      transform: translateY(-2px);
      box-shadow: 0 2px 12px rgba(120, 230, 255, 0.15),
      inset 0 0 8px rgba(120, 230, 255, 0.1);
    }

    &:active {
      transform: translateY(0);
      box-shadow: none;
    }

    .el-icon {
      filter: drop-shadow(0 0 4px rgba(120, 230, 255, 0.3));
    }
  }
}
</style>