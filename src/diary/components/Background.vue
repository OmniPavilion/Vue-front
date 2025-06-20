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

const currentTheme = computed(() => themes[props.theme]);

const backgroundStyles = computed(() => {
  const theme = currentTheme.value;
  const styles: Record<string, string> = {
    '--bg-color': theme.backgroundColor,
    '--edge-color': theme.edgeColor,
    '--grid-animation': props.enableAnimations ? 'gridFlow 15s linear infinite' : 'none'
  };

  if (props.showGrid && theme.showGrid) {
    styles['background-image'] = `linear-gradient(${theme.gridColor} 1px, transparent 1px), linear-gradient(90deg, ${theme.gridColor} 1px, transparent 1px)`;
    styles['background-size'] = `24px 24px`;
  }

  return styles;
});
</script>

<template>
  <div
      class="notebook-background"
      :class="theme"
      :style="backgroundStyles"
  >
    <!-- 仅保留装饰性边缘 -->
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
