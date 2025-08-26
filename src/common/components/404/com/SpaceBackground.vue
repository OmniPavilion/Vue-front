<script setup lang="ts">
import { ref } from 'vue';

const starsCount = ref(500);
const shootingStarsCount = ref(20);
const nebulaeCount = ref(3);

// 生成随机星星样式
const getStarStyle = () => {
  const size = Math.random() * 3 + 1;
  return {
    top: `${Math.random() * 100}%`,
    left: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 5}s`,
    opacity: Math.random() * 0.7 + 0.3,
    animationDuration: `${Math.random() * 10 + 10}s`
  };
};

// 生成流星样式
const getShootingStarStyle = () => {
  const startX = Math.random() * 100;
  const startY = Math.random() * 100;
  const duration = Math.random() * 3 + 2;
  const delay = Math.random() * 15;

  return {
    top: `${startY}%`,
    left: `${startX}%`,
    animationDuration: `${duration}s`,
    animationDelay: `${delay}s`,
    '--end-x': `${- Math.random() * 50 - 50}px`,
    '--end-y': `${- Math.random() * 50 - 50}px`
  };
};

// 生成星云样式
const getNebulaStyle = () => {
  const size = Math.random() * 300 + 200;
  const hue = Math.random() * 60 + 200; // 蓝色到紫色范围

  return {
    width: `${size}px`,
    height: `${size}px`,
    top: `${Math.random() * 100}%`,
    left: `${Math.random() * 100}%`,
    background: `radial-gradient(circle, hsla(${hue}, 80%, 50%, 0.1), transparent`,
    filter: `blur(${Math.random() * 20 + 10}px)`,
    opacity: Math.random() * 0.3 + 0.1,
    animationDuration: `${Math.random() * 60 + 60}s`
  };
};

// 动态创建大恒星
const bigStars = ref([
  { id: 1, size: 6, x: 15, y: 20, color: '#f5d76e', pulse: true },
  { id: 2, size: 4, x: 80, y: 30, color: '#a2ded0', pulse: false },
  { id: 3, size: 5, x: 40, y: 70, color: '#f7ca18', pulse: true }
]);
</script>

<template>
  <div class="space-background">
    <!-- 星云 -->
    <div
        v-for="i in nebulaeCount"
        :key="`nebula-${i}`"
        class="nebula"
        :style="getNebulaStyle()"
    ></div>

    <!-- 普通星星 -->
    <div class="stars">
      <div
          v-for="i in starsCount"
          :key="`star-${i}`"
          class="star"
          :style="getStarStyle()"
      ></div>
    </div>

    <!-- 大恒星 -->
    <div
        v-for="star in bigStars"
        :key="`big-star-${star.id}`"
        class="big-star"
        :class="{ 'pulse': star.pulse }"
        :style="{
        top: `${star.y}%`,
        left: `${star.x}%`,
        width: `${star.size}px`,
        height: `${star.size}px`,
        background: star.color,
        boxShadow: `0 0 ${star.size * 2}px ${star.size}px ${star.color}`
      }"
    ></div>

    <!-- 流星 -->
    <div
        v-for="i in shootingStarsCount"
        :key="`shooting-${i}`"
        class="shooting-star"
        :style="getShootingStarStyle()"
    >
      <div class="tail"></div>
    </div>
  </div>
</template>


<style scoped>
.space-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

/* 星云样式 */
.nebula {
  position: absolute;
  border-radius: 50%;
  animation: drift linear infinite;
  will-change: transform;
}

@keyframes drift {
  0% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(50px, 30px);
  }
  100% {
    transform: translate(0, 0);
  }
}

/* 星星样式 */
.stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  animation:
      twinkle 3s ease-in-out infinite alternate,
      moveStar linear infinite;
  will-change: transform;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

@keyframes moveStar {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(
        calc(var(--move-x, 0) * 100px),
        calc(var(--move-y, 0) * 100px)
    );
  }
}

.star:nth-child(odd) {
  --move-x: 0.5;
  --move-y: 0.2;
}
.star:nth-child(even) {
  --move-x: -0.3;
  --move-y: 0.4;
}
.star:nth-child(3n) {
  --move-x: 0.1;
  --move-y: -0.5;
}
.star:nth-child(5n) {
  --move-x: -0.2;
  --move-y: -0.3;
}

/* 大恒星样式 */
.big-star {
  position: absolute;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  z-index: 2;
}

.big-star.pulse {
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }
}

/* 流星样式 */
.shooting-star {
  position: absolute;
  width: 4px;
  height: 4px;
  background: white;
  border-radius: 50%;
  animation: shoot linear infinite;
  z-index: 3;
}

.shooting-star .tail {
  position: absolute;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, rgba(255,255,255,0.8), transparent);
  transform-origin: left center;
  transform: rotate(45deg);
  left: 2px;
  top: 1px;
}

@keyframes shoot {
  0% {
    transform: translate(0, 0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    /*noinspection CssUnresolvedCustomProperty*/
    transform: translate(var(--end-x), var(--end-y));
    opacity: 0;
  }
}
</style>
