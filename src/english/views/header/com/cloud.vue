<script setup lang="ts">
import { onMounted, ref } from 'vue';

const props = defineProps({
  // 云朵组数量
  count: {
    type: Number,
    default: 6
  },
  // 基础大小
  baseSize: {
    type: Number,
    default: 40
  },
  // 是否启用动画
  animated: {
    type: Boolean,
    default: true
  },
  // 动画速度 (秒)
  duration: {
    type: Number,
    default: 30
  }
});

const cloudsRef = ref<HTMLElement | null>(null);

onMounted(() => {
  if (cloudsRef.value && props.animated) {
    // 为每个云朵设置随机延迟
    const clouds = cloudsRef.value.querySelectorAll('.cloud');
    clouds.forEach(cloud => {
      const delay = Math.random() * props.duration * -1;
      (cloud as HTMLElement).style.animationDelay = `${delay}s`;
    });
  }
});
</script>

<template>
  <div class="clouds-container" ref="cloudsRef">
    <div
        v-for="i in count"
        :key="`cloud-${i}`"
        class="cloud"
        :class="`cloud-${i}`"
        :style="{
        '--size': `${baseSize * (0.8 + Math.random() * 0.4)}px`,
        '--opacity': (0.7 + Math.random() * 0.3).toFixed(2),
        '--duration': `${duration * (0.8 + Math.random() * 0.4)}s`,
        '--x': `${Math.random() * 100}%`,
        '--y': `${Math.random() * 100}%`,
        '--blur': `${Math.random() * 3 + 1}px`
      }"
    ></div>
  </div>
</template>

<!--suppress CssUnresolvedCustomProperty -->
<style scoped>
.clouds-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 0;
}

.cloud {
  position: absolute;
  background-color: rgba(255, 255, 255, var(--opacity, 0.8));
  border-radius: 50%;
  width: var(--size);
  height: calc(var(--size) * 0.6);
  filter: blur(var(--blur, 2px));
  left: var(--x, 10%);
  top: var(--y, 20%);
}

.cloud::before,
.cloud::after {
  content: '';
  position: absolute;
  background-color: inherit;
  border-radius: inherit;
  filter: inherit;
}

.cloud::before {
  width: calc(var(--size) * 0.7);
  height: calc(var(--size) * 0.5);
  top: calc(var(--size) * 0.2);
  left: calc(var(--size) * 0.3);
}

.cloud::after {
  width: calc(var(--size) * 0.5);
  height: calc(var(--size) * 0.4);
  top: calc(var(--size) * -0.1);
  left: calc(var(--size) * 0.6);
}

/* 动画效果 */
.cloud {
  animation: v-bind('animated ? "cloud-move var(--duration) linear infinite" : "none"');
}

@keyframes cloud-move {
  0% {
    transform: translateX(-150%);
  }
  100% {
    transform: translateX(150vw);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .cloud {
    --size: calc(v-bind('baseSize') * 0.8px) !important;
    filter: blur(1px);
  }
}
</style>
