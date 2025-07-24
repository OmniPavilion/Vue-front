<script setup lang="ts">
import {ref, computed, onMounted} from 'vue';
import {
  VideoPlay,
  VideoPause,
  CaretLeft,
  CaretRight,
  Refresh,
  Sort,
  RefreshRight
} from '@element-plus/icons-vue';
import {useMusicPlayStore} from '@/music/stores';
import {audioService} from "@/music/services/audioService";
import {
  LinearNewSoundBroadcastVolumeDown,
  LinearNewSoundBroadcastVolumeOff,
  LinearNewSoundBroadcastVolumeUp
} from "@element-extended-icon-pack/vue";

const playStore = useMusicPlayStore();
const volume = ref(0);
const musicPlayStore = useMusicPlayStore()

// 计算属性
const currentMusic = computed(() => playStore.currentMusic);
const isPlaying = computed(() => playStore.isPlaying);
const currentTime = computed(() => audioService.currentTime);
const duration = computed(() => audioService.duration);

// 播放模式相关
const playModeIcon = computed(() => {
  switch (playStore.playArg.playMode) {
    case 'order': return Refresh;
    case 'random': return Sort;
    case 'loop': return RefreshRight;
    default: return Refresh;
  }
});

const playModeTooltip = computed(() => {
  switch (playStore.playArg.playMode) {
    case 'order': return '顺序播放';
    case 'random': return '随机播放';
    case 'loop': return '单曲循环';
    default: return '顺序播放';
  }
});

// 方法
const togglePlay = () => playStore.togglePlayPause();
const playNext = () => playStore.playNext();
const playPrevious = () => playStore.playPrevious();
const changePlayMode = () => playStore.changePlayMode();

const formatTime = (seconds: number) => {
  if (isNaN(seconds)) return '0:00';
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
};

// 实时拖动时暂存值（不立即生效）
const tempProgress = ref(0);
const handleSeek = (percent: number) => {
  tempProgress.value = percent;
};

// 松手时应用最终值
const commitSeek = () => {
  audioService.currentTime = (tempProgress.value / 100) * duration.value;
};

const handleVolumeChange = (val: number) => {
  audioService.volume = val;
};

const  init = () => {

  if (!musicPlayStore.isInit)  {
    setTimeout(init, 1)
  }
  volume.value = audioService.volume;
};

// 初始化音量
onMounted(() => {
  init()
});
</script>

<template>
  <div class="music-player" :class="{ 'is-playing': isPlaying }">
    <!-- 左侧：歌曲封面和基本信息 -->
    <div class="player-info">
      <el-image
          :src="currentMusic?.pictureUrl"
          fit="cover"
          class="cover"
      />
      <div class="meta">
        <h4 class="title">{{ currentMusic?.title || '暂无歌曲' }}</h4>
        <p class="artist">{{ currentMusic?.singerName || '未知艺术家' }}</p>
      </div>
    </div>

    <!-- 中间：播放控制 -->
    <div class="player-controls">
      <div class="buttons">
        <el-button
            circle
            @click="playPrevious"
        >
          <el-icon>
            <CaretLeft/>
          </el-icon>
        </el-button>

        <el-button
            type="primary"
            circle
            @click="togglePlay"
            :disabled="!currentMusic"
        >
          <el-icon :size="20">
            <VideoPause v-if="isPlaying"/>
            <VideoPlay v-else/>
          </el-icon>
        </el-button>

        <el-button
            circle
            @click="playNext"
        >
          <el-icon>
            <CaretRight/>
          </el-icon>
        </el-button>

        <!-- 播放模式切换按钮 -->
        <el-tooltip :content="playModeTooltip" placement="top">
          <el-button
              circle
              @click="changePlayMode"
              :class="`mode-${playStore.playArg.playMode}`"
          >
            <el-icon>
              <component :is="playModeIcon"/>
            </el-icon>
          </el-button>
        </el-tooltip>
      </div>

      <!-- 进度条 -->
      <div class="progress-container">
        <span class="time">{{ formatTime(currentTime) }}</span>
        <el-slider
            v-model="audioService.progress"
            :show-tooltip="false"
            @input="handleSeek"
            @change="commitSeek"
        />
        <span class="time">{{ formatTime(duration) }}</span>
      </div>
    </div>

    <!-- 右侧：音量控制等 -->
    <div class="player-extra">
      <el-slider
          v-model="volume"
          :max="1"
          :step="0.1"
          :show-tooltip="false"
          :format-tooltip="(val:  number) => `${Math.round(val * 100)}%`"
          @change="handleVolumeChange"
          style="width: 100px;"
      />
      <el-icon :size="20">
        <LinearNewSoundBroadcastVolumeUp v-if="volume > 0.5"/>
        <LinearNewSoundBroadcastVolumeDown v-else-if="volume > 0"/>
        <LinearNewSoundBroadcastVolumeOff v-else/>
      </el-icon>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 使用主题变量
$neon-blue: rgba(120, 230, 255, 0.8);
$glass-bg: rgba(55, 55, 65, 0.75);
$border-thin: 0.5px;
$transition-smooth: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);

.music-player {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 24px;
  background-color: $glass-bg;
  backdrop-filter: blur(12px) brightness(0.96);
  border: $border-thin solid rgba($neon-blue, 0.2);
  border-radius: 10px;
  box-shadow:
      0 0 14px rgba($neon-blue, 0.12),
      inset 0 0 10px rgba($neon-blue, 0.1);
  transition: $transition-smooth;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
            circle at center,
            rgba($neon-blue, 0.05) 0%,
            transparent 70%
    );
    pointer-events: none;
    z-index: -1;
  }

  &:hover {
    box-shadow:
        0 0 18px rgba($neon-blue, 0.15),
        inset 0 0 12px rgba($neon-blue, 0.12);
    transform: translateY(-1px);
  }

  // 左侧：歌曲封面和基本信息
  .player-info {
    display: flex;
    align-items: center;
    gap: 16px;
    min-width: 220px;
    z-index: 1;

    .cover {
      width: 64px;
      height: 64px;
      border-radius: 6px;
      border: $border-thin solid rgba($neon-blue, 0.25);
      box-shadow:
          0 0 8px rgba($neon-blue, 0.1),
          inset 0 0 6px rgba($neon-blue, 0.05);
      transition: $transition-smooth;
      object-fit: cover;
      position: relative;
      overflow: hidden;

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(
                135deg,
                rgba($neon-blue, 0.1) 0%,
                transparent 100%
        );
        pointer-events: none;
      }

      &:hover {
        transform: scale(1.03);
        box-shadow:
            0 0 12px rgba($neon-blue, 0.15),
            inset 0 0 8px rgba($neon-blue, 0.1);
      }
    }

    // 播放模式按钮特殊样式
    .mode-order {
      background-color: rgba(70, 165, 255, 0.3);
      &:hover {
        background-color: rgba(70, 165, 255, 0.4);
      }
    }

    .mode-random {
      background-color: rgba(160, 120, 255, 0.3);
      &:hover {
        background-color: rgba(160, 120, 255, 0.4);
      }
    }

    .mode-loop {
      background-color: rgba(255, 120, 200, 0.3);
      &:hover {
        background-color: rgba(255, 120, 200, 0.4);
      }
    }

    .meta {
      line-height: 1.5;
      overflow: hidden;
      text-shadow: 0 0 4px rgba($neon-blue, 0.1);

      .title {
        margin: 0;
        width: 200px;
        font-size: 16px;
        font-weight: 500;
        color: rgba(240, 240, 255, 0.95);
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        transition: $transition-smooth;
      }

      .artist {
        margin: 4px 0 0;
        font-size: 13px;
        color: rgba(180, 220, 255, 0.8);
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
  }

  // 中间：播放控制
  .player-controls {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
    z-index: 1;

    .buttons {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 16px;

      .el-button {
        position: relative;
        overflow: hidden;
        background-color: rgba(70, 70, 80, 0.6);
        border: $border-thin solid rgba($neon-blue, 0.3);
        box-shadow:
            0 2px 8px rgba(0, 0, 0, 0.2),
            inset 0 1px 1px rgba(255, 255, 255, 0.1);
        transition: $transition-smooth;
        transform: translateZ(0);
        backface-visibility: hidden;

        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(
                  135deg,
                  rgba($neon-blue, 0.2) 0%,
                  transparent 100%
          );
          opacity: 0;
          transition: $transition-smooth;
        }

        &:hover {
          background-color: rgba(80, 80, 90, 0.7);
          border-color: rgba($neon-blue, 0.4);
          box-shadow:
              0 4px 12px rgba(0, 0, 0, 0.25),
              inset 0 1px 2px rgba(255, 255, 255, 0.15);
          transform: translateY(-2px);

          &::before {
            opacity: 1;
          }
        }

        &:active {
          transform: translateY(0);
          transition-duration: 0.1s;
        }

        .el-icon {
          color: rgba(180, 220, 255, 0.9);
          transition: transform 0.15s ease;
        }

        &:hover .el-icon {
          transform: scale(1.15);
        }

        // 主按钮特殊样式
        &--primary {
          background-color: rgba(70, 130, 255, 0.85);
          border-color: rgba(120, 180, 255, 0.4);
          box-shadow:
              0 2px 8px rgba(70, 130, 255, 0.3),
              inset 0 1px 1px rgba(255, 255, 255, 0.2);

          &:hover {
            background-color: rgba(80, 140, 255, 0.95);
            box-shadow:
                0 4px 16px rgba(70, 130, 255, 0.4),
                inset 0 1px 2px rgba(255, 255, 255, 0.3);
          }

          .el-icon {
            color: white;
            filter: drop-shadow(0 0 2px rgba(255, 255, 255, 0.3));
          }
        }

        // 圆形按钮
        &.is-circle {
          box-shadow:
              0 2px 10px rgba(0, 0, 0, 0.2),
              inset 0 1px 1px rgba(255, 255, 255, 0.2);

          &:hover {
            box-shadow:
                0 4px 14px rgba(0, 0, 0, 0.25),
                inset 0 1px 2px rgba(255, 255, 255, 0.3);
          }
        }
      }
    }

    // 进度条容器
    .progress-container {
      display: flex;
      align-items: center;
      gap: 12px;
      width: 100%;

      .time {
        font-size: 13px;
        color: rgba(180, 220, 255, 0.8);
        text-shadow: 0 0 4px rgba($neon-blue, 0.1);
        min-width: 42px;
        text-align: center;
      }

      .el-slider {
        flex: 1;
        height: 4px;

        .el-slider__runway {
          height: 4px;
          background-color: rgba(70, 70, 80, 0.5);
          border: $border-thin solid rgba($neon-blue, 0.2);
          box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.2);

          .el-slider__bar {
            height: 4px;
            background-color: rgba($neon-blue, 0.6);
            box-shadow:
                0 0 6px rgba($neon-blue, 0.4),
                inset 0 0 3px rgba(255, 255, 255, 0.3);
          }

          .el-slider__button {
            width: 14px;
            height: 14px;
            background-color: rgba($neon-blue, 0.9);
            border: 2px solid rgba(255, 255, 255, 0.8);
            box-shadow:
                0 0 8px rgba($neon-blue, 0.6),
                inset 0 0 4px rgba(255, 255, 255, 0.3);

            &:hover {
              transform: scale(1.3);
            }
          }
        }
      }
    }
  }

  // 右侧：音量控制
  .player-extra {
    display: flex;
    align-items: center;
    gap: 10px;
    min-width: 140px;
    z-index: 1;

    .el-slider {
      width: 100px;
      height: 4px;

      .el-slider__runway {
        background-color: rgba(70, 70, 80, 0.5);
        border: $border-thin solid rgba($neon-blue, 0.2);

        .el-slider__bar {
          background-color: rgba($neon-blue, 0.5);
        }

        .el-slider__button {
          background-color: rgba($neon-blue, 0.7);
          border: 2px solid rgba(255, 255, 255, 0.7);
        }
      }
    }

    .el-icon {
      color: rgba(180, 220, 255, 0.8);
      transition: all 0.2s ease;
      cursor: pointer;

      &:hover {
        color: $neon-blue;
        transform: scale(1.15);
        filter: drop-shadow(0 0 4px rgba($neon-blue, 0.3));
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .music-player {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
    backdrop-filter: blur(8px) brightness(0.94);

    .player-info {
      width: 100%;
      justify-content: center;
      text-align: center;
      flex-direction: column;
      gap: 12px;

      .meta {
        width: 100%;
      }
    }

    .player-controls {
      width: 100%;

      .buttons {
        margin-bottom: 8px;
      }
    }

    .player-extra {
      width: 100%;
      justify-content: center;
      margin-top: 8px;
    }
  }
}

// 动画关键帧
@keyframes neon-pulse {
  0%, 100% { opacity: 0.7; }
  50% { opacity: 1; }
}

// 为播放中的歌曲添加特殊效果
.is-playing {
  .cover::after {
    animation: neon-pulse 2s infinite;
  }

  .player-controls .buttons .el-button--primary {
    box-shadow:
        0 0 10px rgba(70, 130, 255, 0.5),
        0 0 20px rgba(70, 130, 255, 0.3),
        inset 0 1px 1px rgba(255, 255, 255, 0.2);
  }
}
</style>
