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
    // 在 Vue 3 的 defineProps 中，当需要为数组或对象指定详细的内部结构时，
    // 单纯使用 type: Array 或 type: Object 无法满足类型检查需求。
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

/**
 * 当前激活的书签索引，null表示没有激活的书签
 */
const activeIndex = ref<number | null>(null);

/**
 * 窗口高度和宽度的响应式数据
 */
const windowHeight = ref(window.innerHeight);
const windowWidth = ref(window.innerWidth);

/**
 * 更新窗口尺寸的函数
 * 当窗口大小改变时调用，更新windowHeight和windowWidth的值
 */
const updateWindowSize = () => {
  windowHeight.value = window.innerHeight;
  windowWidth.value = window.innerWidth;
};

/**
 * 组件挂载时的初始化操作
 */
onMounted(() => {
  // 监听窗口大小变化事件
  window.addEventListener('resize', updateWindowSize);

  // 初始化时根据当前路由设置活动书签
  const currentRouteName = router.currentRoute.value.name;
  const index = props.bookmarks.findIndex(b => b.routeName === currentRouteName);
  if (index !== -1) {
    activeIndex.value = index;
  }
});

/**
 * 组件卸载时的清理操作
 */
onUnmounted(() => {
  // 移除窗口大小变化事件监听器
  window.removeEventListener('resize', updateWindowSize);
});

/**
 * 计算书签高度（响应式）
 * 根据窗口尺寸和书签数量动态计算每个书签的高度
 * 高度在minHeight和maxHeight之间调整
 */
const bookmarkHeight = computed(() => {
  const minHeight = 40;   // 最小高度40px
  const maxHeight = 80;   // 最大高度80px

  // 基础尺寸计算：取窗口宽高中较小值，除以书签数量的1.5倍
  const baseSize = Math.min(windowHeight.value, windowWidth.value) / (props.bookmarks.length * 1.5);

  // 限制在最小和最大高度之间，并添加'px'单位
  return `${Math.max(minHeight, Math.min(maxHeight, baseSize / 2))}px`;
});

/**
 * 计算动态间距（更精细地控制）
 * 根据窗口尺寸、书签数量和spacingVWWeight属性计算书签之间的间距
 * 间距在minSpacing和maxSpacing之间调整
 */
const dynamicSpacing = computed(() => {
  const minSpacing = 8;   // 最小间距8px
  const maxSpacing = 20;  // 最大间距20px

  // 基于视窗尺寸和书签数量的动态计算
  const baseSpacing = Math.min(windowHeight.value, windowWidth.value) / (props.bookmarks.length * 2);
  const spacing = Math.max(minSpacing, Math.min(maxSpacing, baseSpacing));

  // 混合vw和vh单位计算
  const vhRatio = (1 - props.spacingVWWeight) * spacing;  // vh权重部分
  const vwRatio = props.spacingVWWeight * spacing;        // vw权重部分

  // 返回计算后的间距值，使用calc表达式
  return `calc(${vhRatio}px + ${vwRatio}px)`;
});

/**
 * 改进的动态居中计算
 * 根据书签高度、间距和数量计算书签组的起始位置
 * 实现书签组在垂直方向上的居中对齐
 */
const startPosition = computed(() => {
  // 解析书签高度和间距的数值部分
  const heightValue = parseFloat(bookmarkHeight.value);
  const spacingValue = parseFloat(dynamicSpacing.value.replace('calc(', '').replace(')', ''));

  // 计算所有书签的总高度（书签高度*数量 + 间距*(数量-1)）
  const totalHeight = props.bookmarks.length * heightValue + (props.bookmarks.length - 1) * spacingValue;

  // 使用verticalOffset属性和总高度计算居中位置
  return `calc(${props.verticalOffset}% - ${totalHeight / 2}px)`;
});

/**
 * 切换书签状态的函数
 * @param index - 被点击书签的索引
 *
 * 功能：
 * 1. 如果点击的是当前激活的书签，则取消激活（设为null）
 * 2. 如果点击的是其他书签，则激活该书签
 * 3. 跳转到对应书签的路由
 */
const toggleBookmark = (index: number) => {
  const bookmark = props.bookmarks[index];
  if (bookmark) {
    // 切换激活状态：如果当前已激活则设为null，否则设为当前索引
    activeIndex.value = activeIndex.value === index ? null : index;

    // 使用vue-router进行页面跳转
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
