<script setup lang="ts">
import {watch, ref, onMounted, onUnmounted} from 'vue';
import {useHomeStore} from "@/english/stores";
import {tip} from '@/english/constant/tip'


const homeStore = useHomeStore();

const windowWidth = ref(window.innerWidth);
const isShowTip = ref(false);

const handleMenuItemClick = (path: string) => {
  homeStore.activeMenuItem = path;

};

const handleResize = () => {
  isShowTip.value = windowWidth.value < 815;
  windowWidth.value = window.innerWidth
}

// 天气对应的颜色映射
const weatherColors = {
  sunny: ['rgb(100,171,240)', 'rgba(121, 152, 205, 0.95)'],
  cloudy: ['rgba(140, 150, 160, 0.95)', 'rgba(140, 150, 160, 0.95)'],
  rainy: ['rgba(90, 100, 120, 0.95)', 'rgba(90, 100, 120, 0.95)'],
  thunder: ['rgba(80, 90, 110, 0.95)', 'rgba(80, 90, 110, 0.95)'],
  snowy: ['rgba(180, 200, 220, 0.95)', 'rgba(180, 200, 220, 0.95)'],
  windy: ['rgba(130, 150, 180, 0.95)', 'rgba(130, 150, 180, 0.95)']
};

// 动态更新CSS变量
watch(() => homeStore.weatherType, (newVal) => {
  const colors = weatherColors[newVal as keyof typeof weatherColors] || weatherColors.sunny;
  document.documentElement.style.setProperty('--sidebar-bg-start', colors[0]);
  document.documentElement.style.setProperty('--sidebar-bg-end', colors[1]);
}, {immediate: true});


onMounted(() => {
  window.addEventListener('resize', handleResize)
  handleResize()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <el-aside class="sidebar-container">
    <div class="sidebar">
      <ul>
        <li
            v-for="item in tip"
            :key="item.route"
            :class="{ active: homeStore.activeMenuItem === item.route }"
            @click="handleMenuItemClick('/english' + item.route)"
        >
          <router-link :to="'/english' + item.route">
            <el-tooltip
                effect="light"
                :content="item.name"
                placement="right"
                :disabled="isShowTip"
            >
              <div class="menu-content">
                <el-icon class="menu-icon">
                  <component :is="item.icon"/>
                </el-icon>
                <span class="menu-text">{{ item.name }}</span>
              </div>
            </el-tooltip>
          </router-link>
        </li>
      </ul>
    </div>
  </el-aside>
</template>

<style scoped>
:root {
  --sidebar-bg-start: rgba(121, 152, 205, 0.95);
  --sidebar-bg-end: rgba(121, 152, 205, 0.95);
}

.sidebar-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  transition: all 0.3s ease;
}

.sidebar {
  width: 100%;
  height: 100%;
  padding: 20px 0;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
  position: relative;
  overflow-y: auto;
  background: linear-gradient(to bottom,
  var(--sidebar-bg-start) 0%,
  var(--sidebar-bg-end) 100%);
  transition: background 1s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

/* 半透明遮罩效果 */
.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
  z-index: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 1;
}

.sidebar li {
  padding: 0 15px;
  margin: 6px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.sidebar li:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateX(2px);
}

.sidebar li.active {
  background: rgba(147, 197, 253, 0.25);
  border-left: 4px solid #93c5fd;
  box-shadow: inset 2px 0 5px rgba(147, 197, 253, 0.3);
}

.sidebar a {
  text-decoration: none;
  color: #e5e7eb;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding: 12px 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.sidebar li.active a {
  color: white;
  font-weight: 500;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.3);
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
  transition: all 0.3s ease;
}

.sidebar li.active .menu-icon {
  color: #93c5fd;
  transform: scale(1.1);
}

.menu-text {
  flex: 1;
  transition: all 0.3s ease;
}

/* 响应式调整 */
@media (max-width: 816px) {
  .sidebar-container {
    width: 60px !important;
  }

  .menu-text {
    display: none;
  }

  .menu-icon {
    margin-right: 0;
    font-size: 20px;
  }

  .sidebar a {
    justify-content: center;
    padding: 12px 0;
  }
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>
