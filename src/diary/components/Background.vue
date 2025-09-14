<script setup lang="ts">
import { computed } from 'vue';

interface ThemeConfig {
  backgroundColor: string;
  gridColor: string;
  edgeColor: string;
  showGrid: boolean;
}

interface Props {
  theme?: 'classic' | 'modern' | 'dark' | 'vintage';
  showDecorativeEdge?: boolean;
  showGrid?: boolean;
  enableAnimations?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  theme: 'vintage',
  showDecorativeEdge: true,
  showGrid: true,
  enableAnimations: true
});

const themes: Record<string, ThemeConfig> = {
  classic: {
    backgroundColor: '#f9f5e9',
    gridColor: 'rgba(0, 0, 0, 0.05)',
    edgeColor: '#d4c9a8',
    showGrid: true
  },
  modern: {
    backgroundColor: '#ffffff',
    gridColor: 'rgba(0, 0, 0, 0.03)',
    edgeColor: '#e0e0e0',
    showGrid: true
  },
  dark: {
    backgroundColor: '#1a1a1a',
    gridColor: 'rgba(255, 255, 255, 0.05)',
    edgeColor: '#333333',
    showGrid: true
  },
  vintage: {
    backgroundColor: '#f5e8d0',
    gridColor: 'rgba(0, 0, 0, 0.07)',
    edgeColor: '#b8a07a',
    showGrid: true
  }
};

/**
 * 计算当前主题配置
 * 根据传入的 theme 属性值，从 themes 对象中获取对应的主题配置
 */
const currentTheme = computed(() => themes[props.theme]);

/**
 * 计算背景样式
 * 根据当前主题和组件属性动态生成 CSS 变量和背景样式
 */
const backgroundStyles = computed(() => {
  // 获取当前主题配置
  const theme = currentTheme.value;

  // 初始化样式对象，设置基础的 CSS 变量
  const styles: Record<string, string> = {
    // 背景颜色变量，用于设置页面背景色
    '--bg-color': theme.backgroundColor,
    // 装饰边颜色变量，用于设置左右装饰边的颜色
    '--edge-color': theme.edgeColor,
    // 网格动画变量，根据 enableAnimations 属性决定是否启用网格流动动画
    '--grid-animation': props.enableAnimations ? 'gridFlow 15s linear infinite' : 'none'
  };

  // 当同时启用网格显示和当前主题支持网格时，添加网格背景样式
  if (props.showGrid && theme.showGrid) {
    // 设置网格背景图像：由两个线性渐变组成，形成横纵交叉的网格线
    styles['background-image'] = `linear-gradient(${theme.gridColor} 1px, transparent 1px), linear-gradient(90deg, ${theme.gridColor} 1px, transparent 1px)`;
    // 设置背景尺寸为 24x24 像素的网格
    styles['background-size'] = `24px 24px`;
  }

  // 返回计算后的样式对象
  return styles;
});
</script>

<template>
  <div
      class="notebook-background"
      :class="theme"
      :style="backgroundStyles"
  >
    <div
        v-if="showDecorativeEdge"
        class="decorative-edge left"
    ></div>
    <div
        v-if="showDecorativeEdge"
        class="decorative-edge right"
    ></div>
  </div>
</template>

<style scoped>
.notebook-background {
  --bg-color: #f5e8d0;
  --edge-color: #b8a07a;
  --grid-animation: none;

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
  background-color: var(--bg-color);
  animation: var(--grid-animation);
  pointer-events: none;
}

.decorative-edge {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 30px;
  background: var(--edge-color);
  opacity: 0.3;
  pointer-events: none;
}

.decorative-edge.left {
  left: 0;
  border-right: 1px solid var(--edge-color);
}

.decorative-edge.right {
  right: 0;
  border-left: 1px solid var(--edge-color);
}

/* 网格动画 */
@keyframes gridFlow {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 24px 24px;
  }
}

/* 主题特定的样式 */
.notebook-background.classic::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 30px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.05), transparent);
  z-index: 1;
}

.notebook-background.modern {
  --bg-color: #ffffff;
  --edge-color: #e0e0e0;
}

.notebook-background.dark {
  --bg-color: #1a1a1a;
  --edge-color: #333333;
}

.notebook-background.vintage {
  --bg-color: #f5e8d0;
  --edge-color: #b8a07a;
}

/* 主题切换动画 */
.notebook-background.classic,
.notebook-background.modern,
.notebook-background.dark,
.notebook-background.vintage {
  transition:
      background-color 0.5s ease,
      background-image 0.5s ease;
}
</style>
