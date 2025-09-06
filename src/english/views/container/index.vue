<script setup lang="ts">
import { ref } from 'vue'
import Header from "@/english/views/header/index.vue"
import Aside from "@/english/views/aside/index.vue"
import AiChatDialog from "@/english/components/AiChatDialog.vue";

const transitionName = ref('slide-down')



</script>

<template>
  <AiChatDialog></AiChatDialog>

  <div class="common-layout">
    <el-container>
      <el-header height="75px">
        <Header></Header>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <Aside></Aside>
        </el-aside>
        <el-main>
          <div class="main-content">
            <!-- 添加路由过渡动画 -->
            <router-view v-slot="{ Component }">
              <transition
                  :name="transitionName"
                  mode="out-in"
              >
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout {
  height: 100vh;
  width: 100%;
}

.el-container {
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.el-header {
  background-color: #b3c0d1;
  padding: 0;
  display: flex;
  align-items: center;
  z-index: 1;
}

.el-aside {
  background-color: #d3dce6;
}

.el-main {
  background-color: #e9eef3;
  padding: 0;
  overflow: hidden;
}

.main-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 响应式调整 */
@media (max-width: 816px) {
  .el-aside {
    width: 60px !important;
  }
}

/* 路由过渡动画 - 上下切换 */
.slide-down-enter-active,
.slide-down-leave-active,
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.5s ease;
  position: absolute;
  width: 100%;
}

.slide-down-enter-from {
  transform: translateY(100%);
  opacity: 0;
}

.slide-down-leave-to {
  transform: translateY(-30%);
  opacity: 0;
}

.slide-up-enter-from {
  transform: translateY(-30%);
  opacity: 0;
}

.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

/* 优化动画性能 */
.main-content {
  position: relative;
  overflow: hidden;
}

.main-content > * {
  will-change: transform, opacity;
}
</style>
