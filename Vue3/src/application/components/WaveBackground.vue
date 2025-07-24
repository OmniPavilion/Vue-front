<script lang="ts">
// 气泡样式生成
const bubbleStyle = (_: number) => {
  const size = Math.random() * 10 + 5
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${Math.random() * 100}%`,
    animationDuration: `${Math.random() * 15 + 10}s`,
    animationDelay: `${Math.random() * -10}s`,
    opacity: `${Math.random() * 0.3 + 0.2}`
  }
}
</script>

<script setup lang="ts">
import { computed } from 'vue'

// 可配置的波浪参数
const props = defineProps({
  waveHeight: {
    type: Number,
    default: 320
  },
  waveWidth: {
    type: Number,
    default: 1440
  },
  duration: {
    type: Array<number>,
    default: () => [12, 15] // 延长动画时间使更流畅
  },
  colors: {
    type: Array<string>,
    default: () => ['rgba(109, 213, 237, 0.8)', 'rgba(33, 147, 176, 0.8)'] // 半透明颜色
  },
  opacity: {
    type: Array<number>,
    default: () => [0.7, 0.5] // 提高透明度
  }
})

// 更流畅的波浪路径
const waveAnim1 = computed(() => {
  const midY = props.waveHeight * 0.5
  return `M0,${midY}
          C${props.waveWidth*0.25},${midY*1.7}
          ${props.waveWidth*0.75},${midY*0.3}
          ${props.waveWidth},${midY}
          V${props.waveHeight} H0 V${midY} Z;

          M0,${midY*0.8}
          C${props.waveWidth*0.25},${midY*0.4}
          ${props.waveWidth*0.75},${midY*1.6}
          ${props.waveWidth},${midY*0.8}
          V${props.waveHeight} H0 V${midY*0.8} Z;

          M0,${midY}
          C${props.waveWidth*0.25},${midY*1.7}
          ${props.waveWidth*0.75},${midY*0.3}
          ${props.waveWidth},${midY}
          V${props.waveHeight} H0 V${midY} Z`
})

const waveAnim2 = computed(() => {
  const midY = props.waveHeight * 0.6
  return `M0,${midY}
          C${props.waveWidth*0.33},${midY*1.8}
          ${props.waveWidth*0.66},${midY*0.2}
          ${props.waveWidth},${midY}
          V${props.waveHeight} H0 V${midY} Z;

          M0,${midY*0.9}
          C${props.waveWidth*0.33},${midY*0.3}
          ${props.waveWidth*0.66},${midY*1.7}
          ${props.waveWidth},${midY*0.9}
          V${props.waveHeight} H0 V${midY*0.9} Z;

          M0,${midY}
          C${props.waveWidth*0.33},${midY*1.8}
          ${props.waveWidth*0.66},${midY*0.2}
          ${props.waveWidth},${midY}
          V${props.waveHeight} H0 V${midY} Z`
})

const initialPath1 = computed(() => waveAnim1.value.split(';')[0])
const initialPath2 = computed(() => waveAnim2.value.split(';')[0])
</script>

<template>
  <div class="wave-container">
    <svg
        class="waves"
        xmlns="http://www.w3.org/2000/svg"
        :viewBox="`0 0 ${waveWidth} ${waveHeight}`"
        aria-hidden="true"
        preserveAspectRatio="none"
    >
      <defs>
        <linearGradient id="waveGradient" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" :stop-color="colors[0]" />
          <stop offset="50%" :stop-color="colors[1]" />
          <stop offset="100%" :stop-color="colors[0]" />
        </linearGradient>

        <!-- 添加发光效果 -->
        <filter id="glow" x="-30%" y="-30%" width="160%" height="160%">
          <feGaussianBlur stdDeviation="5" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      <!-- 第一层波浪 -->
      <path
          :d="initialPath1"
          fill="url(#waveGradient)"
          :fill-opacity="opacity[0]"
          filter="url(#glow)"
      >
        <animate
            attributeName="d"
            :dur="`${duration[0]}s`"
            repeatCount="indefinite"
            :values="waveAnim1"
            keyTimes="0; 0.5; 1"
            calcMode="spline"
            keySplines="0.42 0 0.58 1; 0.42 0 0.58 1"
        />
      </path>

      <!-- 第二层波浪 -->
      <path
          :d="initialPath2"
          fill="url(#waveGradient)"
          :fill-opacity="opacity[1]"
      >
        <animate
            attributeName="d"
            :dur="`${duration[1]}s`"
            repeatCount="indefinite"
            :values="waveAnim2"
            keyTimes="0; 0.5; 1"
            calcMode="spline"
            keySplines="0.42 0 0.58 1; 0.42 0 0.58 1"
        />
        <!-- 添加波浪边缘高光 -->
        <animate
            attributeName="stroke-opacity"
            values="0;0.3;0"
            dur="10s"
            repeatCount="indefinite"
        />
      </path>
    </svg>

    <!-- 添加泡沫效果 -->
    <div class="bubbles">
      <div v-for="i in 15" :key="i" class="bubble" :style="bubbleStyle(i)"></div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "sass:math"; // 添加这行导入

.wave-container {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

.waves {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  min-height: 300px;
  transform-origin: bottom;
  animation: waveScale 8s ease-in-out infinite alternate;

  path {
    mix-blend-mode: screen;
    stroke-width: 0.5;
    stroke: rgba(255, 255, 255, 0.3);
  }
}

.bubbles {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
}

.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  filter: blur(2px);
  animation: float linear infinite;

  @for $i from 1 through 15 {
    &:nth-child(#{$i}) {
      $size: math.random(15) + 5px; // 改为 math.random()
      width: $size;
      height: $size;
      left: math.random(100) * 1%; // 改为 math.random()
      bottom: -$size;
      animation-duration: math.random(20) + 10s; // 改为 math.random()
      animation-delay: math.random(10) * -1s; // 改为 math.random()
      opacity: math.random(30) * 0.01 + 0.2; // 改为 math.random()
    }
  }
}

@keyframes float {
  to {
    transform: translateY(-100vh);
  }
}

@keyframes waveScale {
  0%, 100% {
    transform: scaleY(1) scaleX(1.02);
  }
  50% {
    transform: scaleY(1.05) scaleX(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .waves {
    min-height: 200px;
    animation: waveScaleMobile 8s ease-in-out infinite alternate;
  }

  @keyframes waveScaleMobile {
    0%, 100% {
      transform: scaleY(1) scaleX(1.01);
    }
    50% {
      transform: scaleY(1.03) scaleX(1);
    }
  }

  .bubble {
    @for $i from 1 through 10 {
      &:nth-child(#{$i}) {
        $size: math.random(10) + 3px; // 改为 math.random()
        width: $size;
        height: $size;
      }
    }
  }
}
</style>
