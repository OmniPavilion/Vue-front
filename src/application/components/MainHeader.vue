<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    required: true
  }
})

// 创建一个响应式引用，用于获取标题元素的DOM节点
const titleRef = ref<HTMLElement | null>(null);

/**
 * 处理鼠标移动事件的函数
 * @param e MouseEvent 鼠标事件对象
 */
function handleMouseMove(e: MouseEvent) {
  // 如果标题元素不存在，则直接返回
  if (!titleRef.value) return;

  // 获取标题元素的位置和尺寸信息
  const rect = titleRef.value.getBoundingClientRect();

  // 获取鼠标在视口中的坐标
  const mouseX = e.clientX;
  const mouseY = e.clientY;

  // 判断鼠标是否在标题区域内
  if (
    mouseX >= rect.left &&      // 鼠标X坐标大于等于元素左边界
    mouseX <= rect.right &&     // 鼠标X坐标小于等于元素右边界
    mouseY >= rect.top &&       // 鼠标Y坐标大于等于元素上边界
    mouseY <= rect.bottom       // 鼠标Y坐标小于等于元素下边界
  ) {
    // 计算鼠标在标题内的百分比位置
    // X轴百分比位置 = (鼠标X坐标 - 元素左边界) / 元素宽度 * 100
    const x = ((mouseX - rect.left) / rect.width) * 100;
    // Y轴百分比位置 = (鼠标Y坐标 - 元素上边界) / 元素高度 * 100
    const y = ((mouseY - rect.top) / rect.height) * 100;

    // 设置背景渐变效果：
    // 1. 径向渐变：以鼠标位置为中心，金色圆形扩散效果
    // 2. 线性渐变：作为背景底色的蓝绿色渐变
    titleRef.value.style.background = `
      radial-gradient(circle at ${x}% ${y}%, #ffd700 30px, transparent 80px),
      linear-gradient(135deg, #2193b0, #6dd5ed)
    `;

    // 设置文本背景裁剪属性，使背景只显示在文字上
    titleRef.value.style.webkitBackgroundClip = 'text';  // Safari兼容
    titleRef.value.style.webkitBackgroundClip = 'text';        // 标准属性

    // 设置文字填充色为透明，显示背景效果
    titleRef.value.style.webkitTextFillColor = 'transparent'; // Safari兼容

    // 设置背景过渡动画，持续时间0.2秒
    titleRef.value.style.transition = 'background 0.2s';

    // 清除滤镜效果（去掉可能存在的边框效果）
    titleRef.value.style.filter = '';
  } else {
    // 当鼠标不在标题区域内时，清除所有特殊样式
    titleRef.value.style.background = '';                 // 清除背景
    titleRef.value.style.webkitBackgroundClip = '';       // 清除背景裁剪
    titleRef.value.style.backgroundClip = '';             // 清除背景裁剪
    titleRef.value.style.webkitTextFillColor = '';        // 恢复文字颜色
    titleRef.value.style.filter = '';                     // 清除滤镜
  }
}

// 组件挂载时添加鼠标移动事件监听器
onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
});

// 组件卸载时移除鼠标移动事件监听器，防止内存泄漏
onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove);
});
</script>

<template>
  <div class="header-container">
    <h1 class="title" ref="titleRef">{{ title }}</h1>
    <p class="subtitle">{{ subtitle }}</p>
  </div>
</template>

<style scoped lang="scss">
.header-container {
  margin-bottom: 2.5rem;
  text-align: center;

  .title {
    font-size: 2.2rem;
    font-weight: 700;
    height: 125px;
    line-height: 125px;
    margin-bottom: 0;
    margin-top: 0;
    color: var(--el-text-color-primary);
    background: linear-gradient(135deg, #2193b0, #6dd5ed);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    transition: background 0.5s, color 0.5s;
    cursor: pointer;
  }

  .subtitle {
    font-size: 1.1rem;
    color: var(--el-text-color-secondary);
    line-height: 1.6;
    max-width: 700px;
    margin: 0 auto;
  }
}

div {
  user-select: none;
}
</style>
