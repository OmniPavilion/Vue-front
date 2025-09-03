<!-- Weather.vue -->
<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import {animate} from "animejs";
import { useHomeStore } from "@/english/stores";

const homeStore = useHomeStore();

const props = defineProps({
  autoChange: {
    type: Boolean,
    default: true
  },
  changeInterval: {
    type: Number,
    default: 30000
  },
  initialWeather: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['weatherChange']);

const weatherTypes = ['sunny', 'cloudy', 'rainy', 'thunder', 'snowy', 'windy'];
const currentWeather = ref(props.initialWeather || weatherTypes[0]);
const transitioning = ref(false);

const changeWeather = (type?: string) => {
  if (transitioning.value) return;

  transitioning.value = true;
  const container = document.querySelector('.weather-container');

  if (container) {
    container.classList.add('fade-out');
  }

  setTimeout(() => {
    if (type) {
      currentWeather.value = type;
    } else {
      const currentIndex = weatherTypes.indexOf(currentWeather.value);
      let newIndex;
      do {
        newIndex = Math.floor(Math.random() * weatherTypes.length);
      } while (newIndex === currentIndex && weatherTypes.length > 1);
      currentWeather.value = weatherTypes[newIndex];
    }

    emit('weatherChange', currentWeather.value);

    if (container) {
      container.classList.remove('fade-out');
      container.classList.add('fade-in');
    }

    setTimeout(() => {
      if (container) {
        container.classList.remove('fade-in');
      }
      transitioning.value = false;
    }, 500);
  }, 500);
};

const initAnimation = () => {
  const move = () => {
    animate('.sun', {
      left: {from: -100, to: 1700},
      y: [
        {from: 40, to: -20, ease:"inOutQuad"},
        {from: -20, to: 40, ease:"inOutQuad"},
      ],
      ease: 'linear',
      duration: 20000,
      loop: true,
    });
  };

  const startSunMovement = () => {
    animate('.sun', {
      right: -100,
      y:{to: 40, ease: 'inOutCubic'},
      ease: 'linear',
      duration: 6000,
      onComplete: move
    });
  };


  animate('.sun', {
    scale: [0, 1],
    ease: 'linear',
    duration: 2000,
    onComplete: startSunMovement
  })

  animate(".sun", {
    boxShadow: ['0 0 20px #ffeb3b', '0 0 40px #ffeb3b'],
    easing: 'easeInOutSine',
    duration: 2000,
    direction: 'alternate',
    alternate: true,
    loop: true
  });
}

onMounted(() => {
  initAnimation()

  if (!props.initialWeather) {
    currentWeather.value = weatherTypes[Math.floor(Math.random() * weatherTypes.length)];
  }

  if (props.autoChange) {
    setInterval(changeWeather, props.changeInterval);
  }
});

defineExpose({
  changeWeather,
  getCurrentWeather: () => currentWeather.value
});

// 监听变化并同步到 Store
watch(currentWeather, (newValue:string) => {
  homeStore.weatherType = newValue;
  console.log('Current weather:', newValue)
});
</script>

<template>
  <div class="weather-container" :class="currentWeather">
    <div class="sky-decoration">
      <!-- 太阳 -->
      <transition name="fade">
        <div class="sun" v-if="currentWeather === 'sunny'"></div>
      </transition>

      <!-- 云朵 -->
      <div class="cloud-group-1">
        <div class="cloud-small"></div>
        <div class="cloud-medium"></div>
        <div class="cloud-large"></div>
      </div>
      <div class="cloud-group-2">
        <div class="cloud-large"></div>
        <div class="cloud-small"></div>
        <div class="cloud-medium"></div>
      </div>

      <!-- 雨 -->
      <transition name="fade">
        <div class="rain" v-if="currentWeather === 'rainy' || currentWeather === 'thunder'">
          <div v-for="i in 30" :key="'rain-'+i" class="raindrop" :style="{
            left: Math.random() * 100 + '%',
            animationDuration: (Math.random() * 0.5 + 0.5) + 's',
            animationDelay: (Math.random()) + 's'
          }"></div>
        </div>
      </transition>

      <!-- 闪电 -->
      <transition name="fade">
        <div class="lightning" v-if="currentWeather === 'thunder'">
          <div class="lightning-bolt"></div>
        </div>
      </transition>

      <!-- 雪 -->
      <transition name="fade">
        <div class="snow" v-if="currentWeather === 'snowy'">
          <div v-for="i in 30" :key="'snow-'+i" class="snowflake" :style="{
            left: Math.random() * 100 + '%',
            animationDuration: (Math.random() * 5 + 5) + 's',
            animationDelay: (Math.random() * 5) + 's',
            opacity: Math.random() * 0.7 + 0.3,
            transform: `scale(${Math.random() * 0.7 + 0.3})`
          }"></div>
        </div>
      </transition>

      <!-- 风 -->
      <transition name="fade">
        <div class="wind-effect" v-if="currentWeather === 'windy'">
          <div v-for="i in 10" :key="'wind-'+i" class="wind-line" :style="{
            top: Math.random() * 100 + '%',
            animationDuration: (Math.random() * 2 + 1) + 's',
            animationDelay: (Math.random() * 2) + 's',
            height: Math.random() * 30 + 10 + 'px'
          }"></div>
        </div>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.weather-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  transition: background 0.5s ease;
  overflow: hidden;
}

/* 天气背景 */
.weather-container.sunny {
  background: linear-gradient(135deg, #6db3f2 0%, #1e69de 100%);
}

.weather-container.cloudy {
  background: linear-gradient(135deg, #a7b8cc 0%, #6b7b8c 100%);
}

.weather-container.rainy, .weather-container.thunder {
  background: linear-gradient(135deg, #5a6b7c 0%, #3a4a5a 100%);
}

.weather-container.snowy {
  background: linear-gradient(135deg, #b8d0e5 0%, #7a9cc6 100%);
}

.weather-container.windy {
  background: linear-gradient(135deg, #8fa8c5 0%, #5d7aa2 100%);
}

/* 淡入淡出 */
.fade-out {
  opacity: 0.5;
  transition: opacity 0.5s ease;
}

.fade-in {
  opacity: 1;
  transition: opacity 0.5s ease;
}

/* 天空元素容器 */
.sky-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 1;
}

/* 太阳样式 */
.sun {
  position: absolute;
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, #fff9c4 0%, #ffeb3b 70%, #ffc107 100%);
  border-radius: 50%;
  top: 20px;
  right: 30%;
  box-shadow: 0 0 40px #ffeb3b;
  animation: sun-glow 4s infinite alternate;
}

@keyframes sun-glow {
  0% { opacity: 0.9; box-shadow: 0 0 30px #ffeb3b; }
  100% { opacity: 1; box-shadow: 0 0 50px #ffeb3b; }
}

/* 云朵基础样式 */
.cloud-small, .cloud-medium, .cloud-large {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  filter: blur(1px);
}

.cloud-small {
  width: 40px;
  height: 25px;
}

.cloud-medium {
  width: 60px;
  height: 35px;
}

.cloud-large {
  width: 80px;
  height: 45px;
}

/* 云朵组1 */
.cloud-group-1 {
  position: absolute;
  top: 30%;
  left: 15%;
  animation: cloud-move 30s linear infinite;
}

.cloud-group-1 .cloud-small {
  top: 0;
  left: 0;
}

.cloud-group-1 .cloud-medium {
  top: 15px;
  left: 30px;
}

.cloud-group-1 .cloud-large {
  top: -10px;
  left: 60px;
}

/* 云朵组2 */
.cloud-group-2 {
  position: absolute;
  top: 20%;
  right: 10%;
  animation: cloud-move 40s linear infinite reverse;
}

.cloud-group-2 .cloud-large {
  top: 0;
  left: 0;
}

.cloud-group-2 .cloud-small {
  top: 20px;
  left: 50px;
}

.cloud-group-2 .cloud-medium {
  top: -10px;
  left: 80px;
}

/* 云朵动画 */
@keyframes cloud-move {
  0% { transform: translateX(-100px); }
  100% { transform: translateX(calc(100vw + 100px)); }
}

/* 雨滴样式 */
.rain {
  position: absolute;
  width: 100%;
  height: 100%;
}

.raindrop {
  position: absolute;
  width: 1px;
  height: 15px;
  background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.7));
  animation: rain-fall linear infinite;
}

@keyframes rain-fall {
  0% { transform: translateY(-50px); opacity: 1; }
  70% { opacity: 0.8; }
  100% { transform: translateY(calc(100vh + 50px)); opacity: 0; }
}

/* 闪电样式 */
.lightning {
  position: absolute;
  width: 100%;
  height: 100%;
}

.lightning-bolt {
  position: absolute;
  width: 3px;
  height: 30px;
  background: white;
  left: 30%;
  top: 10%;
  opacity: 0;
  animation: lightning-flash 8s infinite;
  transform: rotate(20deg);
  box-shadow: 0 0 10px 2px white;
}

@keyframes lightning-flash {
  0%, 30%, 33%, 63%, 65%, 100% { opacity: 0; }
  31%, 32%, 64% { opacity: 1; }
}

/* 雪花样式 */
.snow {
  position: absolute;
  width: 100%;
  height: 100%;
}

.snowflake {
  position: absolute;
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  filter: blur(0.5px);
  animation: snow-fall linear infinite;
}

@keyframes snow-fall {
  0% { transform: translateY(-50px) translateX(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 0.8; }
  100% { transform: translateY(calc(100vh + 50px)) translateX(30px); opacity: 0; }
}

/* 风效果 */
.wind-effect {
  position: absolute;
  width: 100%;
  height: 100%;
}

.wind-line {
  position: absolute;
  width: 100%;
  background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: wind-blow linear infinite;
}

@keyframes wind-blow {
  0% { transform: translateX(-100%); opacity: 0; }
  10% { opacity: 0.5; }
  90% { opacity: 0.3; }
  100% { transform: translateX(100%); opacity: 0; }
}

/* 天气特定效果 */
.weather-container.cloudy .cloud-group-1,
.weather-container.cloudy .cloud-group-2 {
  filter: brightness(0.8);
}

.weather-container.cloudy .cloud-small,
.weather-container.cloudy .cloud-medium,
.weather-container.cloudy .cloud-large {
  background-color: rgba(200, 200, 200, 0.9);
}

.weather-container.windy .cloud-group-1 {
  animation: cloud-move 15s linear infinite;
}

.weather-container.windy .cloud-group-2 {
  animation: cloud-move 20s linear infinite reverse;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sun {
    width: 40px;
    height: 40px;
    right: 20%;
  }

  .cloud-small {
    width: 30px;
    height: 20px;
  }

  .cloud-medium {
    width: 45px;
    height: 25px;
  }

  .cloud-large {
    width: 60px;
    height: 35px;
  }

  .raindrop {
    height: 10px;
  }

  .snowflake {
    width: 5px;
    height: 5px;
  }
}
</style>
