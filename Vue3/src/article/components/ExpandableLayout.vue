<template>
  <div class="expandable-layout">
    <!-- 左侧边栏 -->
    <div
        class="sidebar left-sidebar"
        :style="{ width: leftExpanded ? leftWidth + 'px' : '0px' }"
    >
      <div class="sidebar-header">
        <el-button
            class="close-btn"
            @click="toggleLeft"
            circle
            size="small"
        >
          <el-icon>
            <Close />
          </el-icon>
        </el-button>
      </div>
      <div class="sidebar-content" ref="leftContent">
        <slot name="left"></slot>
      </div>
    </div>

    <!-- 中间内容 -->
    <div class="main-content">
      <slot></slot>
    </div>

    <!-- 右侧边栏 -->
    <div
        class="sidebar right-sidebar"
        :style="{ width: rightExpanded ? rightWidth + 'px' : '0px' }"
    >
      <div class="sidebar-header">
        <el-button
            class="close-btn"
            @click="toggleRight"
            circle
            size="small"
        >
          <el-icon>
            <Close />
          </el-icon>
        </el-button>
      </div>
      <div class="sidebar-content" ref="rightContent">
        <slot name="right"></slot>
      </div>
    </div>

    <el-button
        class="toggle-btn left-toggle"
        @click="toggleLeft"
        circle
    >
      <el-icon>
        <ArrowLeft v-if="leftExpanded"/>
        <ArrowRight v-else/>
      </el-icon>
    </el-button>
    <el-button
        class="toggle-btn right-toggle"
        @click="toggleRight"
        circle
    >
      <el-icon>
        <ArrowRight v-if="rightExpanded"/>
        <ArrowLeft v-else/>
      </el-icon>
    </el-button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUpdated } from 'vue'
import { ArrowLeft, ArrowRight, Close } from "@element-plus/icons-vue";

const leftExpanded = ref(false)
const rightExpanded = ref(false)
const leftWidth = ref(200)
const rightWidth = ref(200)
const leftContent = ref(null)
const rightContent = ref(null)

const toggleLeft = () => {
  leftExpanded.value = !leftExpanded.value
}

const toggleRight = () => {
  rightExpanded.value = !rightExpanded.value
}

const updateSidebarWidths = () => {
  if (leftContent.value && leftContent.value.scrollWidth > 0) {
    leftWidth.value = leftContent.value.scrollWidth + 20 // 加上一些padding
  }

  if (rightContent.value && rightContent.value.scrollWidth > 0) {
    rightWidth.value = rightContent.value.scrollWidth + 20 // 加上一些padding
  }
}

// 初始化和内容变化时更新宽度
onMounted(updateSidebarWidths)
onUpdated(updateSidebarWidths)
</script>

<style scoped lang="scss">
@import "@/article/styles/element/index.scss"; // 导入主题变量

.expandable-layout {
  display: flex;
  height: 100vh; // 使用视口高度
  width: 100%;
  position: relative;
  border: $border-width $border-style $border-color;
  box-shadow:
      inset 0 0 15px rgba(0, 0, 0, 0.05),
      0 3px 6px rgba(0, 0, 0, 0.1);
}

.sidebar {
  position: relative;
  height: 100%;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1); // 更平滑的动画曲线
  overflow: hidden;
  background-color: rgba($notebook-bg, 0.95);
  border: none;
  z-index: 1;
  box-shadow:
      inset 2px 0 5px rgba(0, 0, 0, 0.08),
      inset -1px 0 3px rgba(255, 255, 255, 0.3);

  &-content {
    padding: 15px;
    width: max-content;
    min-width: 100%;
    box-sizing: border-box;
    background-color: transparent;
    height: calc(100% - 40px); // 减去header高度
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 5px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba($notebook-edge, 0.3);
    }
  }
}

.left-sidebar {
  border-right: $border-width $border-style $border-color;
  box-shadow:
      inset 2px 0 5px rgba(0, 0, 0, 0.08),
      inset -1px 0 3px rgba(255, 255, 255, 0.3),
      2px 0 5px rgba(0, 0, 0, 0.05);
}

.right-sidebar {
  border-left: $border-width $border-style $border-color;
  box-shadow:
      inset -2px 0 5px rgba(0, 0, 0, 0.08),
      inset 1px 0 3px rgba(255, 255, 255, 0.3),
      -2px 0 5px rgba(0, 0, 0, 0.05);
}

.sidebar-header {
  display: flex;
  justify-content: flex-end;
  padding: 8px 12px;
  border-bottom: $border-width $border-style $border-color;
  background-color: rgba($notebook-edge, 0.08);
  height: 40px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.close-btn {
  margin-left: auto;
  color: $primary-color !important;
  background-color: transparent !important;
  border: none !important;
  transition: all 0.2s ease;
  width: 24px;
  height: 24px;

  &:hover {
    color: darken($primary-color, 10%) !important;
    background-color: rgba($notebook-edge, 0.1) !important;
    transform: rotate(90deg);
  }

  .el-icon {
    transition: transform 0.3s ease;
  }
}

.main-content {
  flex: 1;
  height: 100%;
  overflow: auto;
  padding: 20px;
  box-shadow:
      inset 0 2px 5px rgba(0, 0, 0, 0.05),
      inset 0 -2px 5px rgba(0, 0, 0, 0.05);
  position: relative;

  &::before, &::after {
    content: "";
    position: absolute;
    top: 0;
    bottom: 0;
    width: 1px;
    background-color: rgba($notebook-edge, 0.2);
  }

  &::before {
    left: 0;
  }

  &::after {
    right: 0;
  }
}

.toggle-btn {
  position: absolute;
  top: 20%;
  width: 30px;
  height: 60px;
  border-radius: 0 15px 15px 0;
  color: $text-primary !important;
  background-color: rgba($notebook-bg, 0.9) !important;
  border: $border-width $border-style $border-color !important;
  box-shadow:
      2px 0 5px rgba(0, 0, 0, 0.1),
      inset 1px 0 2px rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: rgba($notebook-edge, 0.1) !important;
    width: 32px;

    .el-icon {
      transform: scale(1.2);
    }
  }

  .el-icon {
    transition: all 0.2s ease;
  }
}

.left-toggle {
  left: 0;
  border-radius: 0 15px 15px 0;

  &:hover {
    left: -2px;
  }
}

.right-toggle {
  right: 0;
  border-radius: 15px 0 0 15px;

  &:hover {
    right: -2px;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .toggle-btn {
    width: 24px;
    height: 50px;

    &:hover {
      width: 26px;
    }
  }

  .sidebar-header {
    padding: 6px 10px;
    height: 36px;
  }

  .sidebar-content {
    padding: 10px;
    height: calc(100% - 36px);
  }

  .main-content {
    padding: 15px;
  }
}
</style>