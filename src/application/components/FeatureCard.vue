<script setup lang="ts">
import {ElButton, ElCard, ElIcon} from 'element-plus'
import type { Feature } from "@/application/assets/data/features";

defineProps<{
  feature: Feature
}>()

function handleCardClick(feature: Feature) {
  if (feature.available && feature.link) {
    window.open(feature.link, '_blank')
  }
}
</script>

<template>
  <el-card
      :shadow="feature.available ? 'hover' : 'never'"
      class="feature-card"
      :class="{
      'disabled-card': !feature.available,
      'clickable-card': feature.available
    }"
      @click="handleCardClick(feature)"
  >
    <div class="feature-icon">
      <el-icon>
        <component :is="feature.icon" />
      </el-icon>
    </div>
    <h3 class="feature-name">{{ feature.name }}</h3>
    <p class="feature-desc">{{ feature.desc }}</p>
    <div class="feature-action">
      <el-button
          v-if="!feature.available"
          disabled
          class="coming-btn"
      >
        敬请期待
      </el-button>
    </div>
  </el-card>
</template>

<style scoped lang="scss">
.feature-card {
  height: 100%;
  min-height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  padding: 24px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.1),
      inset 0 0 0 1px rgba(255, 255, 255, 0.2);
  cursor: default;
  position: relative;
  overflow: hidden;
  margin-bottom: 8px;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: transparent;
    transition: background 0.3s ease;
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.15) 0%,
            rgba(255, 255, 255, 0) 50%,
            rgba(255, 255, 255, 0.15) 100%
    );
    pointer-events: none;
    z-index: -1;
  }

  &.clickable-card {
    cursor: pointer;

    &:hover {
      transform: translateY(-8px);
      background: rgba(255, 255, 255, 0.3);
      box-shadow:
          0 12px 24px rgba(0, 0, 0, 0.15),
          inset 0 0 0 1px rgba(255, 255, 255, 0.4);
      border-color: var(--el-color-primary-light-5);

      &::before {
        background: var(--el-color-primary);
      }

      .feature-icon .el-icon {
        transform: scale(1.1);
        background: rgba(64, 158, 255, 0.3);
        box-shadow:
            0 4px 12px rgba(64, 158, 255, 0.2),
            inset 0 0 0 1px rgba(255, 255, 255, 0.3);
      }

      .feature-name {
        color: var(--el-color-primary);
      }
    }

    &:active {
      transform: translateY(-4px);
      transition: all 0.1s ease;
    }
  }

  &.disabled-card {
    opacity: 0.9;
    background: rgba(200, 200, 200, 0.15);
    cursor: not-allowed;

    .feature-icon .el-icon {
      color: var(--el-text-color-disabled);
      background: rgba(0, 0, 0, 0.05);
    }

    .feature-name {
      color: var(--el-text-color-disabled);
    }

    .feature-desc {
      color: var(--el-text-color-disabled);
    }
  }

  .feature-icon {
    text-align: center;
    margin-bottom: 1.2rem;
    transition: transform 0.3s ease;

    .el-icon {
      color: var(--el-color-primary);
      font-size: 3rem;
      padding: 16px;
      background: rgba(64, 158, 255, 0.15);
      backdrop-filter: blur(5px);
      border-radius: 50%;
      transition: all 0.3s ease;
      box-shadow:
          0 2px 8px rgba(64, 158, 255, 0.1),
          inset 0 0 0 1px rgba(255, 255, 255, 0.2);
    }
  }

  .feature-name {
    font-size: 1.4rem;
    font-weight: 600;
    text-align: center;
    margin-bottom: 1rem;
    color: var(--el-text-color-primary);
    transition: color 0.3s ease;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }

  .feature-desc {
    color: var(--el-text-color-secondary);
    text-align: center;
    margin: 1rem 0 1.5rem;
    flex-grow: 1;
    font-size: 1rem;
    line-height: 1.6;
    text-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
  }

  .feature-action {
    position: absolute;
    top: 1rem;
    right: 1rem;
    text-align: center;

    .coming-btn {
      border-radius: 12px;
      padding: 4px 12px;
      font-size: 0.8rem;
      background: rgba(0, 0, 0, 0.05);
      backdrop-filter: blur(5px);
      color: var(--el-text-color-disabled);
      border: 1px solid rgba(255, 255, 255, 0.1);
      cursor: not-allowed;
      box-shadow: none;
    }
  }
}

@media (max-width: 768px) {
  .feature-card {
    min-height: 240px;
    padding: 20px;
    backdrop-filter: blur(8px);

    .feature-icon .el-icon {
      font-size: 2.5rem;
      padding: 12px;
    }

    .feature-name {
      font-size: 1.3rem;
    }

    .feature-desc {
      font-size: 0.95rem;
    }
  }
}
</style>
