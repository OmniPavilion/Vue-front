<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElCollapseTransition } from 'element-plus'

const position = ref({ x: 50, y: 50 })
const startPosition = ref({ x: 50, y: 50 })
const isDragging = ref(false)
const startPos = ref({ x: 0, y: 0 })
const container = ref(null)
const containerSize = ref({ width: 0, height: 0 })
const velocity = ref({ x: 0, y: 0 }) // 新增：速度跟踪
const lastPosition = ref({ x: 0, y: 0 }) // 新增：最后位置记录
const animationFrameId = ref(null) // 新增：动画帧ID

const containerStyle = computed(() => ({
  left: `${position.value.x}px`,
  top: `${position.value.y}px`,
  width: containerSize.value.width ? `${containerSize.value.width}px` : 'auto',
  height: containerSize.value.height ? `${containerSize.value.height}px` : 'auto',
  transition: isDragging.value ? 'none' : 'transform 0.3s ease-out' // 新增：平滑过渡
}))

const handleStyle = computed(() => ({
  left: `${position.value.x - 10}px`,
  top: `${position.value.y - 10}px`,
  cursor: isDragging.value ? 'grabbing' : 'grab'
}))

// 新增：惯性动画函数
const animateInertia = () => {
  // 如果速度很小，停止动画
  if (Math.abs(velocity.value.x) < 0.1 && Math.abs(velocity.value.y) < 0.1) {
    velocity.value = { x: 0, y: 0 }
    return
  }

  let newX = position.value.x + velocity.value.x
  let newY = position.value.y + velocity.value.y

  // 边界约束
  if (container.value) {
    newX = Math.max(0, Math.min(newX, window.innerWidth - containerSize.value.width))
    newY = Math.max(0, Math.min(newY, window.innerHeight - containerSize.value.height))

    // 碰到边界时反弹
    if (newX <= 0 || newX >= window.innerWidth - containerSize.value.width) {
      velocity.value.x *= -0.4 // 反弹并减速
    }
    if (newY <= 0 || newY >= window.innerHeight - containerSize.value.height) {
      velocity.value.y *= -0.4 // 反弹并减速
    }
  }

  position.value = { x: newX, y: newY }

  // 减速效果
  velocity.value = {
    x: velocity.value.x * 0.95,
    y: velocity.value.y * 0.95
  }

  animationFrameId.value = requestAnimationFrame(animateInertia)
}

onMounted(() => {
  window.addEventListener('mousemove', handleDrag)
  window.addEventListener('touchmove', handleDrag, { passive: false })
  window.addEventListener('mouseup', stopDrag)
  window.addEventListener('touchend', stopDrag)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleDrag)
  window.removeEventListener('touchmove', handleDrag)
  window.removeEventListener('mouseup', stopDrag)
  window.removeEventListener('touchend', stopDrag)
  cancelAnimationFrame(animationFrameId.value) // 清理动画帧
})

const startDrag = (e) => {
  isDragging.value = true
  const clientX = e.clientX || e.touches[0].clientX
  const clientY = e.clientY || e.touches[0].clientY

  startPosition.value = {
    x: clientX,
    y: clientY
  }

  startPos.value = {
    x: clientX - position.value.x,
    y: clientY - position.value.y
  }

  // 停止任何正在进行的惯性动画
  cancelAnimationFrame(animationFrameId.value)
  velocity.value = { x: 0, y: 0 }

  e.preventDefault()
}

const handleDrag = (e) => {
  if (!isDragging.value) return

  const clientX = e.clientX || e.touches[0].clientX
  const clientY = e.clientY || e.touches[0].clientY

  // 计算速度（当前位置 - 上次位置）
  if (lastPosition.value.x && lastPosition.value.y) {
    velocity.value = {
      x: (clientX - lastPosition.value.x) * 1.5, // 乘以系数增加速度感
      y: (clientY - lastPosition.value.y) * 1.5
    }
  }

  let newX = clientX - startPos.value.x
  let newY = clientY - startPos.value.y

  // 边界约束
  if (container.value) {
    newX = Math.max(0, Math.min(newX, window.innerWidth - containerSize.value.width))
    newY = Math.max(0, Math.min(newY, window.innerHeight - containerSize.value.height))
  }

  position.value = { x: newX, y: newY }
  lastPosition.value = { x: clientX, y: clientY }

  e.preventDefault()
}

const stopDrag = () => {
  if (!isDragging.value) return

  isDragging.value = false

  // 只有在有足够速度时才启动惯性动画
  if (Math.abs(velocity.value.x) > 2 || Math.abs(velocity.value.y) > 2) {
    animationFrameId.value = requestAnimationFrame(animateInertia)
  } else {
    velocity.value = { x: 0, y: 0 }
  }

  // 重置最后位置
  lastPosition.value = { x: 0, y: 0 }
}

defineExpose({
  setPosition: (x, y) => {
    position.value = { x, y }
  },
  getPosition: () => position.value,
})

const isShow = ref(false)

const toggle = () => {
  if (Math.abs(startPosition.value.x - position.value.x) < 10 &&
      Math.abs(startPosition.value.y - position.value.y) < 10) {
    isShow.value = !isShow.value
  }
}
</script>

<template>
  <div class="floating-wrapper">
    <div
        class="drag-handle"
        :style="handleStyle"
        @mousedown="startDrag"
        @mouseup="toggle"
        @touchstart="startDrag"
    ></div>
    <div
        v-show="isShow"
        class="floating-container"
        :style="containerStyle"
        ref="container"
    >
      <el-collapse-transition v-show="isShow">
        <div class="content-area">
          <slot></slot>
        </div>
      </el-collapse-transition>
    </div>
  </div>
</template>

<style scoped>
.floating-wrapper {
  position: relative;
}

.floating-container {
  position: fixed;
  z-index: 9999;
  border-radius: 4px;
  overflow: hidden;
  will-change: transform;
  min-width: 100px;
  min-height: 50px;
  transform: translateZ(0); /* 启用硬件加速 */
}

.drag-handle {
  position: fixed;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  background-color: rgba(70, 165, 255, 0.35);
  border: 0.5px solid rgba(120, 230, 255, 0.4);
  color: white;
  box-shadow:
      0 0 10px rgba(70, 165, 255, 0.3),
      inset 0 0 6px rgba(255, 255, 255, 0.2);
  transform: translateZ(0); /* 启用硬件加速 */
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.drag-handle:hover {
  background-color: rgba(70, 165, 255, 0.45);
  transform: scale(1.1) translateZ(0);
  box-shadow:
      0 0 14px rgba(70, 165, 255, 0.4),
      inset 0 0 8px rgba(255, 255, 255, 0.3);
}

.drag-handle::after {
  content: "⋯";
  font-size: 16px;
  line-height: 1;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 0 6px rgba(120, 230, 255, 0.5);
}

.content-area {
  padding: 10px;
}
</style>
