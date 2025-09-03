<script setup lang="ts">
import { useHomeStore } from '@/english/stores'
import { ref, onMounted, onUnmounted } from 'vue'
import {type Tip, tip} from '@/english/constant/tip'
import { Sunny, Cloudy, Lightning,  Pouring, Drizzling, Sunrise} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router';

const router = useRouter();
const homeStore = useHomeStore()

const cards = ref<Tip[]>(tip.slice(1))
const isScrolling = ref(false)
let scrollTimer: number | null = null

const navigateTo = (path: string) => {
  router.push(path);
  homeStore.activeMenuItem = path;
};

// 处理滚动效果
const handleScroll = () => {
  isScrolling.value = true
  if (scrollTimer) clearTimeout(scrollTimer)
  scrollTimer = setTimeout(() => {
    isScrolling.value = false
  }, 100) as unknown as number
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (scrollTimer) clearTimeout(scrollTimer)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div style="width: 100%;height: 100%;overflow-x: auto">

    <div class="start-container">
      <!-- 欢迎卡片 -->
      <div class="welcome-card" :class="{'scrolling': isScrolling}">
        <div class="welcome-background"></div>
        <h1>欢迎来到英韵阁</h1>
        <p class="subtitle">一个集智能词库、记忆训练、单元测试，错题管理和AI辅助于一体的英语学习综合平台</p>

        <div class="weather-info">
          <div class="weather-icon">
            <el-icon v-if="homeStore.weatherType === 'cloudy'"><Cloudy /></el-icon>
            <el-icon v-if="homeStore.weatherType === 'rainy'"><Pouring /></el-icon>
            <el-icon v-if="homeStore.weatherType === 'snowy'"><Drizzling /></el-icon>
            <el-icon v-if="homeStore.weatherType === 'sunny'"><Sunny /></el-icon>
            <el-icon v-if="homeStore.weatherType === 'thunder'"><Lightning /></el-icon>
            <el-icon v-if="homeStore.weatherType === 'windy'"><Sunrise /></el-icon>
          </div>
        </div>

        <!-- 装饰性学习图标 -->
        <div class="decorative-icons">
          <div class="deco-icon">📖</div>
          <div class="deco-icon">✏️</div>
          <div class="deco-icon">🔍</div>
          <div class="deco-icon">🌎</div>
        </div>
      </div>

      <!-- 功能特点 -->
      <div class="features-container">
        <h2>系统功能</h2>
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in [
          {icon: '📚', title: '单词学习', desc: '提供四级、六级和通用单词库，支持多种学习模式'},
          {icon: '📝', title: '智能测试', desc: '自动单词选择题，巩固已学单词'},
          {icon: '🤖', title: 'AI助手', desc: '提供AI聊天对话框，可以让ai帮你查询，添加单词以及翻译句子'}
        ]" :key="index">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
            <div class="feature-hover-effect"></div>
          </div>
        </div>
      </div>

      <!-- 快速访问 -->
      <div class="quick-start">
        <h2>快速访问</h2>
        <div class="card-container">
          <div v-for="(item, index) in cards" :key="index">
            <el-card
                class="access-card"
                :style="{ '--card-color': item.color }"
                shadow="hover"
                @click="navigateTo('/english' + item.route)"
            >
              <div class="card-content">
                <div class="card-icon">
                  <el-icon :size="36" :color="item.color">
                    <component :is="item.icon" />
                  </el-icon>
                </div>
                <div class="card-text">
                  <h3>{{ item.name }}</h3>
                  <p>{{ item.desc }}</p>
                </div>
              </div>
              <div class="card-hover-indicator"></div>
            </el-card>
          </div>
        </div>
      </div>

      <!-- 视觉分隔元素 -->
      <div class="section-divider">
        <div class="divider-line"></div>
        <div class="divider-icon">🌟</div>
        <div class="divider-line"></div>
      </div>

      <!-- 鼓舞人心的名言 -->
      <div class="inspirational-quote">
        <p>"学习一门新语言就是拥有第二个灵魂。"</p>
        <span>- 查理曼大帝</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  user-select: none;
}

.start-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
  color: #333;
  animation: fadeIn 0.5s ease-out;
  position: relative;
  z-index: 2;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.welcome-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 15px;
  padding: 40px;
  text-align: center;
  margin-bottom: 40px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.4) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.5s ease;
}

.welcome-card:hover::before {
  opacity: 1;
}

.welcome-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.welcome-card h1 {
  font-size: 2.5rem;
  margin-bottom: 15px;
  color: #2c3e50;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
  position: relative;
  z-index: 2;
}

.subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin-bottom: 20px;
  line-height: 1.6;
  position: relative;
  z-index: 2;
}

.weather-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  background: rgba(255,255,255,0.7);
  padding: 15px 25px;
  border-radius: 50px;
  width: fit-content;
  margin: 20px auto 0;
  position: relative;
  z-index: 2;
  backdrop-filter: blur(5px);
}

.weather-icon {
  font-size: 3rem;
  color: #f39c12;
}

/* 装饰性图标 */
.decorative-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.deco-icon {
  font-size: 2rem;
  opacity: 0.7;
  animation: bounce 3s ease-in-out infinite;
}

.deco-icon:nth-child(1) { animation-delay: 0s; }
.deco-icon:nth-child(2) { animation-delay: 0.5s; }
.deco-icon:nth-child(3) { animation-delay: 1s; }
.deco-icon:nth-child(4) { animation-delay: 1.5s; }

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.features-container {
  margin-bottom: 40px;
  position: relative;
}

.features-container h2,
.quick-start h2,
.about-section h2,
.project-section h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 30px;
  color: #2c3e50;
  position: relative;
}

.features-container h2::after,
.quick-start h2::after,
.about-section h2::after,
.project-section h2::after {
  content: '';
  display: block;
  width: 80px;
  height: 4px;
  background: linear-gradient(to right, #3498db, #9b59b6);
  margin: 10px auto 0;
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.feature-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: default;
  border: 1px solid rgba(0,0,0,0.05);
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  border-color: rgba(52, 152, 219, 0.3);
}

.feature-hover-effect {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(52, 152, 219, 0.1), transparent);
  transition: left 0.7s ease;
}

.feature-card:hover .feature-hover-effect {
  left: 100%;
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
  transition: transform 0.3s ease;
  position: relative;
  z-index: 2;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.feature-card h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #3498db;
  position: relative;
  z-index: 2;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.5;
  position: relative;
  z-index: 2;
}

.quick-start {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 40px;
  margin-bottom: 40px;
  position: relative;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.access-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  /*noinspection CssUnresolvedCustomProperty*/
  border-left: 4px solid var(--card-color);
  position: relative;
  overflow: hidden;
}

.access-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15) !important;
}

.card-hover-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 3px;
  transition: width 0.3s ease;
}

.access-card:hover .card-hover-indicator {
  width: 100%;
}

.card-content {
  display: flex;
  align-items: center;
  padding: 15px 0;
}

.card-icon {
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.access-card:hover .card-icon {
  transform: scale(1.1);
}

.card-text h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 6px;
}

.card-text p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

/* 分隔元素 */
.section-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 50px 0;
}

.divider-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, #3498db, transparent);
  flex: 1;
  max-width: 200px;
}

.divider-icon {
  margin: 0 20px;
  font-size: 1.5rem;
  opacity: 0.7;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.7; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

/* 名言部分 */
.inspirational-quote {
  text-align: center;
  padding: 30px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  margin: 40px 0;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(0,0,0,0.05);
  position: relative;
}

.inspirational-quote::before {
  content: '"';
  position: absolute;
  top: 10px;
  left: 20px;
  font-size: 4rem;
  color: rgba(52, 152, 219, 0.2);
  font-family: serif;
}

.inspirational-quote p {
  font-size: 1.2rem;
  font-style: italic;
  color: #2c3e50;
  margin-bottom: 10px;
  position: relative;
  z-index: 2;
}

.inspirational-quote span {
  color: #7f8c8d;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .start-container {
    padding: 20px;
  }

  .welcome-card {
    padding: 30px 20px;
  }

  .welcome-card h1 {
    font-size: 2rem;
  }

  .features-grid,
  .card-container {
    grid-template-columns: 1fr;
  }

  .weather-info {
    flex-direction: column;
    text-align: center;
    padding: 15px;
  }

  .decorative-icons {
    flex-wrap: wrap;
  }

  .floating-circle {
    display: none;
  }

  .section-divider {
    margin: 30px 0;
  }
}
</style>