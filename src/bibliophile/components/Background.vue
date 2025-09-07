<template>
  <div class="ai-reader-background" :class="{ 'dark-mode': darkMode }">
    <!-- 背景装饰元素 -->
    <div class="floating-books">
      <div v-for="(book, index) in floatingBooks" :key="index" class="floating-book" :style="book.style">
        <div class="book-cover" :style="{ backgroundColor: book.color }">
          <div class="book-spine"></div>
          <div class="book-pages"></div>
        </div>
      </div>
    </div>

    <!-- 科技感线条 -->
    <div class="tech-lines">
      <div v-for="(line, index) in techLines" :key="'line-' + index" class="tech-line" :style="line.style"></div>
    </div>

    <!-- 数据粒子效果 -->
    <div class="data-particles">
      <div v-for="(particle, index) in particles" :key="'particle-' + index" class="particle" :style="particle.style">
        <span class="particle-text">{{ particle.text }}</span>
      </div>
    </div>

    <!-- 内容插槽 -->
    <div class="content-wrapper">
      <slot></slot>
    </div>

    <!-- 模式切换按钮 -->
    <div class="theme-toggle">
      <el-button
          :icon="darkMode ? 'Sunny' : 'Moon'"
          circle
          @click="toggleTheme"
          class="toggle-btn"
          :title="darkMode ? '切换到亮色模式' : '切换到暗色模式'"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElButton } from 'element-plus'
import { useFileStore } from "@/bibliophile/stores";

const fileStore = useFileStore()

interface FloatingBook {
  style: {
    left: string
    top: string
    animationDelay: string
    transform: string
  }
  color: string
}

interface TechLine {
  style: {
    left: string
    width: string
    animationDelay: string
    height: string
  }
}

interface Particle {
  style: {
    left: string
    top: string
    animationDelay: string
    fontSize: string
    opacity: string
  }
  text: string
}

const darkMode = ref(false)
const floatingBooks = ref<FloatingBook[]>([])
const techLines = ref<TechLine[]>([])
const particles = ref<Particle[]>([])

// 书本颜色
const bookColors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#f9ca24', '#6c5ce7', '#a29bfe']

// 初始化浮动书本
const initFloatingBooks = () => {
  const books: FloatingBook[] = []
  for (let i = 0; i < 8; i++) {
    books.push({
      style: {
        left: `${Math.random() * 90 + 5}%`,
        top: `${Math.random() * 80 + 10}%`,
        animationDelay: `${Math.random() * 5}s`,
        transform: `rotate(${Math.random() * 30 - 15}deg)`
      },
      color: bookColors[Math.floor(Math.random() * bookColors.length)]
    })
  }
  floatingBooks.value = books
}

// 初始化科技线条
const initTechLines = () => {
  const lines: TechLine[] = []
  for (let i = 0; i < 12; i++) {
    lines.push({
      style: {
        left: `${Math.random() * 100}%`,
        width: `${Math.random() * 100 + 50}px`,
        animationDelay: `${Math.random() * 3}s`,
        height: `${Math.random() * 2 + 1}px`
      }
    })
  }
  techLines.value = lines
}

// 初始化数据粒子
const initParticles = () => {
  const newParticles: Particle[] = []
  const texts = ['AI', 'ML', 'NLP', 'PDF', 'TXT', 'DOC', 'READ', 'BOOK', 'TEXT', 'DATA']

  for (let i = 0; i < 20; i++) {
    newParticles.push({
      style: {
        left: `${Math.random() * 100}%`,
        top: `${Math.random() * 100}%`,
        animationDelay: `${Math.random() * 10}s`,
        fontSize: `${Math.random() * 12 + 10}px`,
        opacity: `${Math.random() * 0.5 + 0.2}`
      },
      text: texts[Math.floor(Math.random() * texts.length)]
    })
  }
  particles.value = newParticles
}

// 切换主题
const toggleTheme = () => {
  darkMode.value = !darkMode.value
  // 可以在这里添加主题持久化逻辑
  fileStore.aiReaderTheme = darkMode.value ? 'dark' : 'light'
  console.log('主题切换', darkMode.value)
}

onMounted(() => {
  initFloatingBooks()
  initTechLines()
  initParticles()

})
</script>

<style scoped>
.ai-reader-background {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  transition: all 0.5s ease;
}

.ai-reader-background.dark-mode {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
}

/* 浮动书本动画 */
.floating-books {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-book {
  position: absolute;
  animation: float 8s ease-in-out infinite;
}

.book-cover {
  width: 40px;
  height: 60px;
  border-radius: 4px;
  position: relative;
  transform-style: preserve-3d;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.book-spine {
  position: absolute;
  left: 0;
  top: 5px;
  width: 5px;
  height: 50px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 2px 0 0 2px;
}

.book-pages {
  position: absolute;
  right: 0;
  top: 2px;
  width: 35px;
  height: 56px;
  background: linear-gradient(90deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.3) 100%);
  border-radius: 0 4px 4px 0;
}

/* 科技线条 */
.tech-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.tech-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
  animation: scan 4s linear infinite;
}

/* 数据粒子 */
.data-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 300;
  animation: particleFloat 15s ease-in-out infinite;
}

.particle-text {
  font-family: 'Courier New', monospace;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

/* 内容区域 */
.content-wrapper {
  position: relative;
  z-index: 10;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 主题切换按钮 */
.theme-toggle {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.toggle-btn {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.toggle-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 动画定义 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes scan {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(200%);
  }
}

@keyframes particleFloat {
  0%, 100% {
    transform: translateY(0) translateX(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) translateX(50px) rotate(360deg);
    opacity: 0;
  }
}

/* 暗色模式调整 */
.dark-mode .book-cover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
}

.dark-mode .tech-line {
  background: linear-gradient(90deg, transparent, rgba(100, 200, 255, 0.4), transparent);
}

.dark-mode .particle {
  color: rgba(200, 220, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .floating-book {
    transform: scale(0.8);
  }

  .particle {
    font-size: 10px !important;
  }
}
</style>