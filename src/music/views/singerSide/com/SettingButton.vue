<script setup>
import {ref, onMounted} from 'vue'
import {Setting, Edit, Check, Close, RefreshRight, Clock} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {useMusicPlayStore, usePlayStore} from '@/music/stores'

const playStore = usePlayStore()
const musicPlayStore = useMusicPlayStore()
const drawerVisible = ref(false)
const direction = ref('rtl')
const musicRootPath = ref('')
const fileInput = ref(null)
const isEditing = ref(false)
const tempPath = ref('')

onMounted(async () => {
  await loadCurrentPath()
})

const loadCurrentPath = async () => {
  try {
    await playStore.getRootPath()
    musicRootPath.value = playStore.rootPath
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
    musicRootPath.value = path
    event.target.value = ''
  }
}

const startEditing = () => {
  tempPath.value = musicRootPath.value
  isEditing.value = true
}

const saveEditing = async () => {
  isEditing.value = false
  await saveSettings()
}

const cancelEditing = () => {
  musicRootPath.value = tempPath.value
  isEditing.value = false
}

const saveSettings = async () => {
  try {
    if (!musicRootPath.value) {
      ElMessage.warning('请先选择音乐目录')
      return
    }
    const res = await playStore.setRootPath(musicRootPath.value)
    if (res.code !== 1) {
      ElMessage.error(res.message)
      return
    }
    ElMessage.success('目录设置成功')
  } catch (error) {
    ElMessage.error('保存目录失败: ' + error.message)
  }
}

const resetRootPath = async () => {
  try {
    await playStore.resetRootPath()
    await loadCurrentPath()
    ElMessage.success('目录已重置为默认值')
  } catch (error) {
    ElMessage.error('重置目录失败: ' + error.message)
  }
}

const formatPlayTime = (seconds) => {
  if (!seconds || seconds <= 0) return '00:00:00'

  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  return [
    hours.toString().padStart(2, '0'),
    minutes.toString().padStart(2, '0'),
    secs.toString().padStart(2, '0')
  ].join(':')
}
</script>

<template>
  <div class="setting-button-container">
    <el-tooltip effect="dark" content="设置音乐目录" placement="bottom">
      <el-button
          class="setting-btn"
          :icon="Setting"
          circle
          @click="openSettings"
      />
    </el-tooltip>

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
          <div class="play-time-display">
            <span class="neon-text">
              <el-icon class="neon-icon"><Clock /></el-icon>
              听歌时间: {{ formatPlayTime(musicPlayStore.playArg.playDuration) }}
            </span>
          </div>

          <div class="input-container">
            <el-input
                v-model="musicRootPath"
                placeholder="请输入音乐目录路径"
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
                    :disabled="!musicRootPath"
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
        </div>

        <div class="footer-actions">
          <el-button
              @click="saveSettings"
              type="primary"
              :disabled="!musicRootPath"
              class="save-btn"
          >
            保存设置
          </el-button>
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

  .save-btn, .reset-btn {
    min-width: 100px;
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }
}

.play-time-display {
  margin-bottom: 16px;
  padding: 10px 14px;
  background-color: rgba(65, 65, 75, 0.6) !important;
  backdrop-filter: blur(8px);
  border: 0.5px solid rgba(120, 230, 255, 0.2) !important;
  border-radius: 6px;
  box-shadow:
      inset 0 0 8px rgba(120, 230, 255, 0.1),
      0 0 10px rgba(120, 230, 255, 0.08);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);

  .neon-text {
    color: rgba(180, 220, 255, 0.9);
    font-size: 14px;
    text-shadow: 0 0 6px rgba(120, 230, 255, 0.3);
  }

  .neon-icon {
    color: rgba(120, 230, 255, 0.8);
    margin-right: 8px;
    filter: drop-shadow(0 0 4px rgba(120, 230, 255, 0.4));
  }

  &:hover {
    background-color: rgba(70, 70, 80, 0.7) !important;
    box-shadow:
        inset 0 0 10px rgba(120, 230, 255, 0.15),
        0 0 12px rgba(120, 230, 255, 0.12);

    .neon-text {
      text-shadow: 0 0 8px rgba(120, 230, 255, 0.4);
    }

    .neon-icon {
      color: rgba(120, 230, 255, 1);
      filter: drop-shadow(0 0 6px rgba(120, 230, 255, 0.6));
    }
  }
}
</style>
