<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useAiChatStore } from '@/english/stores'
import {ElMessage} from "element-plus";

const aiStore = useAiChatStore()
const inputMessage = ref('')
const chatContainer = ref<HTMLElement | null>(null)

// 拖动图标相关状态
const draggableIcon = ref<HTMLElement | null>(null)
const isDraggingIcon = ref(false)
const iconPosX = ref(20)
const iconPosY = ref(20)
const iconStartX = ref(0)
const iconStartY = ref(0)

// 对话框拖动和缩放相关状态
const dialogRef = ref<HTMLElement | null>(null)
const isResizing = ref(false)
const dialogPosX = ref(20)
const dialogPosY = ref(80)
const dialogWidth = ref(350)
const dialogHeight = ref(500)
const minWidth = 300
const minHeight = 400

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || aiStore.isLoading) {
    ElMessage.warning('请输入内容' as any)
    return
  }

  if (aiStore.isStreaming) {
    ElMessage.warning('请等待AI处理完成' as any)
    return
  }

  await aiStore.chat(inputMessage.value)
  inputMessage.value = ''

  await nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 清除对话
const clearChat = () => {
  if (aiStore.isLoading) {
    ElMessage.warning('请等待AI处理完成' as any)
    return
  }
  aiStore.clearChat()
}

// 处理键盘事件
const handleKeyDown = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// 图标拖动相关函数
const startIconDrag = (e: MouseEvent | TouchEvent) => {
  isDraggingIcon.value = true
  const clientX = e instanceof MouseEvent ? e.clientX : e.touches[0].clientX
  const clientY = e instanceof MouseEvent ? e.clientY : e.touches[0].clientY
  iconStartX.value = clientX - iconPosX.value
  iconStartY.value = clientY - iconPosY.value
}

const onIconDrag = (e: MouseEvent | TouchEvent) => {
  if (!isDraggingIcon.value) return

  e.preventDefault()
  const clientX = e instanceof MouseEvent ? e.clientX : (e as TouchEvent).touches[0].clientX
  const clientY = e instanceof MouseEvent ? e.clientY : (e as TouchEvent).touches[0].clientY

  // 限制在窗口范围内
  const maxX = window.innerWidth - (draggableIcon.value?.offsetWidth || 50)
  const maxY = window.innerHeight - (draggableIcon.value?.offsetHeight || 50)

  iconPosX.value = Math.min(Math.max(0, clientX - iconStartX.value), maxX)
  iconPosY.value = Math.min(Math.max(0, clientY - iconStartY.value), maxY)

  // 同步更新对话框位置
  dialogPosX.value = iconPosX.value
  dialogPosY.value = iconPosY.value + 60

}

const stopIconDrag = () => {
  isDraggingIcon.value = false
}

// 对话框缩放相关函数
const startResize = (e: MouseEvent) => {
  e.preventDefault()
  e.stopPropagation()
  isResizing.value = true
  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
}

const onResize = (e: MouseEvent) => {
  if (!isResizing.value) return

  const newWidth = Math.max(minWidth, e.clientX + 18 - dialogPosX.value)
  const newHeight = Math.max(minHeight, e.clientY + 18 - dialogPosY.value)

  dialogWidth.value = newWidth
  dialogHeight.value = newHeight
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
}

// 处理外部点击
const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  const isClickOnIcon = draggableIcon.value?.contains(target)
  const isClickOnDialog = dialogRef.value?.contains(target)

  // 如果点击的不是图标也不是对话框，且对话框是打开的，则关闭对话框
  if (!isClickOnIcon && !isClickOnDialog && aiStore.isOpen) {
    aiStore.closeChat()
  }
}

// scrollToBottom方法
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 监听对话框打开状态
watch(() => aiStore.isOpen, (newVal) => {
  if (newVal) {
    // 对话框打开时，滚动到底部
    scrollToBottom()
  }
})



// 添加事件监听
onMounted(() => {
  window.addEventListener('mousemove', onIconDrag)
  window.addEventListener('touchmove', onIconDrag)
  window.addEventListener('mouseup', stopIconDrag)
  window.addEventListener('touchend', stopIconDrag)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onIconDrag)
  window.removeEventListener('touchmove', onIconDrag)
  window.removeEventListener('mouseup', stopIconDrag)
  window.removeEventListener('touchend', stopIconDrag)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <!-- 可拖动的AI图标 -->
  <div
      ref="draggableIcon"
      class="ai-icon"
      :style="{ left: `${iconPosX}px`, top: `${iconPosY}px` }"
      @mousedown="startIconDrag"
      @touchstart="startIconDrag"
      @click.stop="aiStore.openChat"
  >
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12 8V4H8" />
      <rect width="16" height="12" x="4" y="8" rx="2" />
      <path d="M2 14h2" />
      <path d="M20 14h2" />
      <path d="M15 13v2" />
      <path d="M9 13v2" />
    </svg>
  </div>

  <!-- AI对话框 -->
  <div
      v-if="aiStore.isOpen"
      ref="dialogRef"
      class="ai-dialog"
      :style="{
      left: `${dialogPosX}px`,
      top: `${dialogPosY}px`,
      width: `${dialogWidth}px`,
      height: `${dialogHeight}px`
    }"
      @click.stop
  >
    <div class="ai-header">
      <h3>AI助手</h3>
      <div class="header-actions">
        <button @click.stop="clearChat" class="clear-btn" title="清除对话">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 6h18" />
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
          </svg>
        </button>
        <button @click.stop="aiStore.closeChat" class="close-btn">×</button>
      </div>
    </div>

    <div ref="chatContainer" class="chat-container">
      <!-- 只显示ADD_WORD模式的消息 -->
      <div
          v-for="msg in aiStore.getWordChatMessages()"
          :key="msg.id"
          class="message"
          :class="{
          'user-message': msg.type === 'USER',
          'ai-message': msg.type === 'ASSISTANT',
          'system-message': msg.type === 'SYSTEM'
        }"
      >
        <div class="avatar" :class="{
          'user-avatar': msg.type === 'USER',
          'ai-avatar': msg.type === 'ASSISTANT',
          'system-avatar': msg.type === 'SYSTEM'
        }">
          {{ msg.type === 'USER' ? '你' : msg.type === 'ASSISTANT' ? 'AI' : '系统' }}
        </div>
        <div class="message-content" v-html="msg.content"></div>
      </div>

      <!-- 加载状态 -->
      <div v-if="aiStore.isStreaming" class="message ai-message">
        <div class="avatar ai-avatar">AI</div>
        <div class="message-content loading">
          <div class="dot-flashing"></div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <textarea
          v-model="inputMessage"
          @keydown="handleKeyDown"
          placeholder="输入消息..."
          :disabled="aiStore.isStreaming"
      ></textarea>
      <button
          @click.stop="sendMessage"
          :disabled="!inputMessage.trim() || aiStore.isStreaming"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="22" y1="2" x2="11" y2="13"></line>
          <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
        </svg>
      </button>
    </div>

    <!-- 缩放手柄 -->
    <div class="resize-handle" @mousedown.stop="startResize"></div>
  </div>
</template>

<style scoped>
.ai-icon {
  position: fixed;
  width: 50px;
  height: 50px;
  background-color: #4f46e5;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: transform 0.2s;
  user-select: none;
  touch-action: none;
}

.ai-icon:active {
  cursor: grabbing;
  transform: scale(0.95);
}

.ai-icon svg {
  width: 24px;
  height: 24px;
}

.ai-dialog {
  position: fixed;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  z-index: 999;
  overflow: hidden;
  resize: none;
  cursor: default;
}

.ai-header {
  padding: 12px 16px;
  background-color: #4f46e5;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  user-select: none;
}

.ai-header h3 {
  margin: 0;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.close-btn, .clear-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn {
  font-size: 20px;
  width: 24px;
  height: 24px;
}

.clear-btn {
  opacity: 0.8;
  transition: opacity 0.2s;
}

.clear-btn:hover {
  opacity: 1;
}

.chat-container {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message {
  display: flex;
  gap: 8px;
  max-width: 90%;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-message {
  align-self: flex-start;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.user-avatar {
  background-color: #4f46e5;
  color: white;
}

.ai-avatar {
  background-color: #e5e7eb;
  color: #4b5563;
}

.message-content {
  padding: 8px 12px;
  border-radius: 12px;
  line-height: 1.4;
}

.user-message .message-content {
  background-color: #4f46e5;
  color: white;
  border-bottom-right-radius: 0;
}

.ai-message .message-content {
  background-color: #f3f4f6;
  color: #111827;
  border-bottom-left-radius: 0;
}

.message-content:active {
  cursor: grabbing;

}

.input-area {
  display: flex;
  padding: 12px;
  border-top: 1px solid #e5e7eb;
  gap: 8px;
}

.input-area textarea {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  resize: none;
  min-height: 40px;
  max-height: 120px;
  font-family: inherit;
}

.input-area textarea:focus {
  outline: none;
  border-color: #4f46e5;
}

.input-area button {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 8px;
  background-color: #4f46e5;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.input-area button:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

.loading {
  display: flex;
  align-items: center;
  min-height: 40px;
}

.dot-flashing {
  position: relative;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9ca3af;
  color: #9ca3af;
  animation: dot-flashing 1s infinite linear alternate;
  animation-delay: 0.5s;
}

.dot-flashing::before, .dot-flashing::after {
  content: "";
  display: inline-block;
  position: absolute;
  top: 0;
  width: 10px;
  height: 10px;
  border-radius: 5px;
  background-color: #9ca3af;
  color: #9ca3af;
}

.dot-flashing::before {
  left: -15px;
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 0s;
}

.dot-flashing::after {
  left: 15px;
  animation: dot-flashing 1s infinite alternate;
  animation-delay: 1s;
}

.resize-handle {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 15px;
  height: 15px;
  background: #4f46e5;
  cursor: nwse-resize;
  z-index: 1000;
}

@keyframes dot-flashing {
  0% {
    background-color: #9ca3af;
  }
  50%, 100% {
    background-color: #d1d5db;
  }
}

@media (max-width: 480px) {
  .ai-dialog {
    width: 100vw !important;
    height: 100vh !important;
    max-width: none;
    top: 0 !important;
    left: 0 !important;
    border-radius: 0;
  }

  .ai-icon {
    bottom: 20px;
    right: 20px;
    left: auto;
    top: auto;
  }

  .resize-handle {
    display: none;
  }
}
</style>
