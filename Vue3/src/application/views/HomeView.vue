<script setup lang="ts">
import {ElRow, ElCol, ElCard} from 'element-plus'
import WaveBackground from '@/application/components/WaveBackground.vue'
import FeatureCard from '@/application/components/FeatureCard.vue'
import MainHeader from '@/application/components/MainHeader.vue'
import {features} from '@/application/assets/data/features'
</script>

<template>
  <div class="home-container">
    <div class="wave-background">
      <WaveBackground/>
    </div>


        <main class="main-content">
      <el-card shadow="hover" class="main-card">
        <MainHeader
            title="Omni Pavilion 多功能工具集"
            subtitle="个人常用工具集合，集成记事本、音乐播放器、PDF阅读器等实用功能，持续扩展中。"
        />

        <div class="features-scroll-container">
          <el-row
              :gutter="24"
              justify="center"
              class="features-grid"
          >
            <el-col
                v-for="feature in features"
                :key="feature.name"
                :xs="24"
                :sm="12"
                :md="8"
                :lg="6"
                class="feature-col"
            >
              <FeatureCard :feature="feature"/>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </main>
  </div>
</template>

<style scoped lang="scss">
.wave-background {
  position:  absolute;
  width: 100vw;
  height: 100vh;
  z-index: 1;
}

.home-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(109, 213, 237, 0.8) 0%, rgba(33, 147, 176, 0.8) 100%);
  overflow-x: hidden;
  box-sizing: border-box;
  backdrop-filter: blur(2px);
}

.main-content {
  position: relative;
  height: 100vh;
  z-index: 2;
  padding: 3rem;
  width: 100%;
  max-width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-card {
  padding: 2rem 2.5rem;
  border-radius: 16px;
  background: linear-gradient(
          to bottom,
          rgba(255, 255, 255, 0.25) 0%,
          rgba(255, 255, 255, 0) 100%
  );
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      inset 0 0 0 1px rgba(255, 255, 255, 0.2);
  overflow: hidden;
  width: 100%;
  max-width: 1800px;
  height: 100%;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.1) 0%,
            rgba(255, 255, 255, 0) 100%
    );
    pointer-events: none;
    z-index: -1;
  }
}

.features-scroll-container {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 12px;
  margin: 0 -12px;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(255, 255, 255, 0.4);
    border-radius: 4px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);

    &:hover {
      background-color: rgba(255, 255, 255, 0.6);
    }
  }

  &::-webkit-scrollbar-track {
    background-color: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
  }
}

.features-grid {
  padding: 0 12px;
  min-height: min-content;
}

.feature-col {
  margin-bottom: 2.5rem;
  padding: 0 12px;
  transition: transform 0.3s ease;
  min-width: 0;

  &:hover {
    transform: translateY(-5px);
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 1rem;
  }

  .main-card {
    padding: 1.5rem;
    backdrop-filter: blur(8px);
  }

  .feature-col {
    margin-bottom: 1.5rem;

    &:hover {
      transform: none;
    }
  }
}

@media (max-width: 480px) {
  .main-card {
    padding: 1rem;
    border-radius: 12px;
  }

  .feature-col {
    margin-bottom: 1rem;
  }
}

* {
  user-select: none;
}
</style>
