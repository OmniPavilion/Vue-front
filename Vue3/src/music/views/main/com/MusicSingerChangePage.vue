<template>
  <div class="page-container">
    <!-- 页面切换器 -->
    <div class="page-switcher">
      <el-radio-group
          v-model="generalStore.CurrentPage"
          size="large"
          class="switcher-group"
      >
        <el-radio-button value="music" class="switcher-button">
          <el-icon>
            <Headset/>
          </el-icon>
          <span>音乐</span>
        </el-radio-button>
        <el-radio-button value="singer" class="switcher-button">
          <el-icon>
            <User/>
          </el-icon>
          <span>歌手</span>
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 音乐页面 -->
    <div v-if="generalStore.CurrentPage === 'music'" class="music-page">
        <SongIndex></SongIndex>
    </div>

    <!-- 歌手页面 -->
    <div v-else class="singer-page">
          <SingerIndex></SingerIndex>
    </div>
  </div>
</template>

<script setup lang="ts">
import {Headset, User} from '@element-plus/icons-vue'
import {useGeneralStore} from '@/music/stores'
import SongIndex from "@/music/views/song/index.vue"
import SingerIndex from "@/music/views/singer/index.vue"

const generalStore = useGeneralStore()
</script>

<style scoped lang="scss">
.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.page-switcher {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding: 10px 0px;
  background-color: rgba(45, 45, 55, 0.01);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  border: 0.5px solid rgba(120, 230, 255, 0.14);
  box-shadow: 0 0 10px rgba(120, 230, 255, 0.18),
  inset 0 0 10px rgba(120, 230, 255, 0.1);

  .switcher-group {
    background-color: rgba(60, 60, 70, 0.6);
    border: 0.5px solid rgba(120, 230, 255, 0.2);
    border-radius: 6px;
    padding: 4px;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2),
    inset 0 1px 1px rgba(255, 255, 255, 0.1);

    .switcher-button {
      margin: 0;
      border: none;
      background: transparent;
      color: rgba(220, 230, 240, 0.9);
      transition: all 0.25s ease;

      &.is-active {
        background-color: rgba(70, 165, 255, 0.35);
        color: white;
        box-shadow: 0 0 10px rgba(70, 165, 255, 0.3),
        inset 0 0 6px rgba(255, 255, 255, 0.2);
        text-shadow: 0 0 6px rgba(120, 230, 255, 0.4);

        .el-icon {
          color: white;
        }
      }

      &:hover:not(.is-active) {
        background-color: rgba(120, 230, 255, 0.18);
      }

      .el-icon {
        margin-right: 6px;
        color: rgba(180, 200, 220, 0.8);
        transition: all 0.25s ease;
      }

      span {
        font-weight: 500;
      }
    }
  }
}

.music-page {
  flex: 1;
  padding: 0 20px;
  height: 80%;
}

.singer-page {
  flex: 1;
  padding: 0 20px;
  height: 80%;
}

.singer-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}


</style>
