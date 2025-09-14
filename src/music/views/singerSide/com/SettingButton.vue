<script setup>
import {ref} from 'vue'
import {Setting, Clock, DocumentChecked} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {useMusicPlayStore, useMusicStore} from '@/music/stores'

const musicPlayStore = useMusicPlayStore()
const musicStore = useMusicStore()
const drawerVisible = ref(false)
const direction = ref('rtl')


const openSettings = async () => {
  drawerVisible.value = true
}

const checkFile = async () => {
  try {
    const res = await musicStore.checkFile()
    if (res.code !== 1) {
      ElMessage.error(res.message)
      return
    }
    ElMessage.success('检查完成, 文件保存完整')
  } catch (error) {
    ElMessage.error('检查文件失败: ' + error.message)
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
    <el-tooltip effect="dark" content="设置" placement="bottom">
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

          <div class="check-file-container">
            <el-tooltip effect="dark" content="检查文件完整性" placement="top">
              <el-button
                  @click="checkFile"
                  class="check-file-btn"
                  :icon="DocumentChecked"
                  circle
              />
            </el-tooltip>
          </div>
        </div>
      </div>
    </el-drawer>
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
      box-shadow:
          0 2px 12px rgba(120, 230, 255, 0.15),
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
