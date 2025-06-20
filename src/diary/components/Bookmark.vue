<script setup lang="ts">
import {ref, onMounted, onUnmounted, computed, type Component} from 'vue';
import { useRouter } from 'vue-router';
import {
  ElIcon,
  ElBadge,
  ElTooltip
} from 'element-plus';
import {
  HomeFilled,
  Notebook,
  Calendar,
  Document
} from '@element-plus/icons-vue';

const router = useRouter();

const props = defineProps({
  // 书签配置数组
  bookmarks: {
    type: Array as () => Array<{
      name: string;
      routeName: string;
      color?: string;
      icon?: Component;
      badge?: number | string;
      tooltip?: string;
    }>,
    default: () => [
      {
        name: '首页',
        routeName: 'home',
        color: '#FF9AA2',
        icon: HomeFilled,
        tooltip: '返回首页'
      },
      {
        name: '日志',
        routeName: 'log',
        color: '#FFB7B2',
        icon: Notebook,
        tooltip: '查看日志'
      },
      {
        name: '计划',
        routeName: 'plan',
        color: '#FFDAC1',
        icon: Calendar,
        tooltip: '管理计划'
      },
      {
        name: '代表事项',
        routeName: 'note',
        color: '#E2F0CB',
        icon: Document,
        tooltip: '编辑代办'
      }
    ],
    validator: (value: any[]) => value.length > 0 && value.length <= 10
  },
  // 初始露出的宽度
  initialWidth: {
    type: String,
    default: '1.5vw'
  },
  // 展开后的宽度
  expandedWidth: {
    type: String,
    default: '15vw'
  },
  // 书签间距的vw权重
  spacingVWWeight: {
    type: Number,
    default: 0.5,
    validator: (value: number) => value >= 0 && value <= 1
  },
  // 垂直偏移量（从顶部开始的百分比）
  verticalOffset: {
    type: Number,
    default: 40,
    validator: (value: number) => value >= 0 && value <= 100
  }
});

const activeIndex = ref<number | null>(null);
const windowHeight = ref(window.innerHeight);
const windowWidth = ref(window.innerWidth);

const updateWindowSize = () => {
  windowHeight.value = window.innerHeight;
  windowWidth.value = window.innerWidth;
};

onMounted(() => {
  window.addEventListener('resize', updateWindowSize);
  // 初始化时根据当前路由设置活动书签
  const currentRouteName = router.currentRoute.value.name;
  const index = props.bookmarks.findIndex(b => b.routeName === currentRouteName);
  if (index !== -1) {
    activeIndex.value = index;
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', updateWindowSize);
});

// 计算书签高度（响应式）
const bookmarkHeight = computed(() => {
  const minHeight = 40;
  const maxHeight = 80;
  const baseSize = Math.min(windowHeight.value, windowWidth.value) / (props.bookmarks.length * 1.5);
  return `${Math.max(minHeight, Math.min(maxHeight, baseSize / 2))}px`;
});

// 计算动态间距（更精细地控制）
const dynamicSpacing = computed(() => {
  const minSpacing = 8;
  const maxSpacing = 20;

  // 基于视窗尺寸和书签数量的动态计算
  const baseSpacing = Math.min(windowHeight.value, windowWidth.value) / (props.bookmarks.length * 2);
  const spacing = Math.max(minSpacing, Math.min(maxSpacing, baseSpacing));

  // 混合vw和vh单位
  const vhRatio = (1 - props.spacingVWWeight) * spacing;
  const vwRatio = props.spacingVWWeight * spacing;

  return `calc(${vhRatio}px + ${vwRatio}px)`;
});

// 改进的动态居中计算
const startPosition = computed(() => {
  const heightValue = parseFloat(bookmarkHeight.value);
  const spacingValue = parseFloat(dynamicSpacing.value.replace('calc(', '').replace(')', ''));
  const totalHeight = props.bookmarks.length * heightValue + (props.bookmarks.length - 1) * spacingValue;

  return `calc(${props.verticalOffset}% - ${totalHeight / 2}px)`;
});

const toggleBookmark = (index: number) => {
  const bookmark = props.bookmarks[index];
  if (bookmark) {
    activeIndex.value = activeIndex.value === index ? null : index;
    router.push({ name: bookmark.routeName });
  }
};
</script>

<template>
  <div class="bookmarks-container">
    <el-tooltip
        v-for="(bookmark, index) in bookmarks"
        :key="index"
        :content="bookmark.tooltip || bookmark.name"
        placement="left"
        :show-after="500"
    >
      <div
          class="bookmark"
          :class="{ active: activeIndex === index }"
          :style="{
          backgroundColor: bookmark.color || '#B5EAD7',
          top: `calc(${startPosition} + ${index} * (${bookmarkHeight} + ${dynamicSpacing}))`,
          '--initial-width': initialWidth,
          '--expanded-width': expandedWidth,
          height: bookmarkHeight
        }"
          @click="toggleBookmark(index)"
      >
        <el-badge
            :value="bookmark.badge"
            :hidden="!bookmark.badge"
            class="badge-wrapper"
        >
          <div class="bookmark-content">
            <el-icon :size="18" class="bookmark-icon">
              <component :is="bookmark.icon" />
            </el-icon>
            <span class="bookmark-text">{{ bookmark.name }}</span>
          </div>
        </el-badge>
      </div>
    </el-tooltip>
  </div>
</template>

<style scoped>
.bookmarks-container {
  position: fixed;
  right: 0;
  top: 0;
  height: 100vh;
  width: 0;
  z-index: 1000;
}

.bookmark {
  position: absolute;
  right: 0;
  transform: translateX(calc(100% - var(--initial-width)));
  width: var(--expanded-width);
  border-top-left-radius: 0.5em;
  border-bottom-left-radius: 0.5em;
  display: flex;
  align-items: center;
  padding-left: 0.8em;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: -2px 2px 5px rgba(0, 0, 0, 0.2);
  color: #333;
  font-weight: bold;
  overflow: hidden;
}

.bookmark.active {
  transform: translateX(0);
}

.bookmark:hover {
  filter: brightness(1.1);
  transform: translateX(calc(100% - var(--initial-width) - 5px));
}

.bookmark.active:hover {
  transform: translateX(0);
}

.bookmark-content {
  display: flex;
  align-items: center;
  gap: 0.5em;
  width: 100%;
}

.bookmark-icon {
  flex-shrink: 0;
  opacity: 0.8;
}

.bookmark-text {
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.2s ease 0.1s;
  font-size: clamp(0.8rem, 1vw, 1.2rem);
}

.bookmark.active .bookmark-text {
  opacity: 1;
}

.badge-wrapper :deep(.el-badge__content) {
  top: 8px;
  right: 8px;
  transform: scale(0.8);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .bookmark {
    --initial-width: 3vw;
    --expanded-width: 20vw;
    border-top-left-radius: 0.3em;
    border-bottom-left-radius: 0.3em;
    padding-left: 0.6em;
  }

  .bookmark-text {
    font-size: clamp(0.7rem, 1.5vw, 1rem);
  }

  .bookmark-icon {
    font-size: 16px;
  }
}

@media (max-height: 600px) {
  .bookmark {
    height: 35px !important;
    --expanded-width: 18vw;
  }

  .bookmark-text {
    font-size: 0.7rem;
  }

  .bookmark-icon {
    font-size: 14px;
  }
}
</style>
