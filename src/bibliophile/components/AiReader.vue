<script setup lang="ts">
import {onMounted, ref, nextTick, watch} from 'vue'
import {useFileStore, useAiReadStore} from '@/bibliophile/stores'
import {ElMessage, ElButton, ElInput, ElCollapseTransition, ElMessageBox, ElTooltip} from 'element-plus'
import 'element-plus/es/components/collapse-transition/style/css'
import {ChatDotRound, Delete, UserFilled, WarningFilled, DArrowLeft, Download, View, Hide} from "@element-plus/icons-vue";
import {markDownIt} from "@/common/utils/tools";

const aiReadStore = useAiReadStore()
const fileStore = useFileStore()
const userInput = ref<string>('')
const showPdf = ref<boolean>(true)
const messagesEndRef = ref<HTMLElement>()

// 添加暗色模式状态
const darkMode = ref(true)

// 发送消息给AI
const sendMessage = async () => {
  if (!userInput.value.trim()) {
    ElMessage.warning('请输入消息' as any)
    return
  }

  const text = userInput.value
  userInput.value = ''

  await aiReadStore.chatWithFile(text)
}

// 切换PDF显示/隐藏
const toggleFile = () => {
  showPdf.value = !showPdf.value
}

// 下载函数
const downloadAiResponse = () => {
  aiReadStore.downloadChatHistory()
};

// 格式化文件大小
const formatFileSize = (size: number): string => {
  if (size === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 角色名称映射
const getRoleName = (type: string) => {
  const map: Record<string, string> = {
    'USER': '用户',
    'ASSISTANT': 'AI助手',
    'SYSTEM': '系统'
  }
  return map[type] || type
}

// 格式化时间
const formatTime = (timestamp: string) => {
  return new Date(timestamp).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 删除处理函数
const handleDeleteMessage = async (messageId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await aiReadStore.deleteChatById(messageId)
    if (res.code !== 1) {
      ElMessage.error(res.message as any)
      return
    }
    ElMessage.success('消息删除成功' as any)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除消息失败' as any)
    }
  }
}

const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有对话吗？', '清空确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await aiReadStore.deleteChatHistory()
    if (res.code !== 1) {
      ElMessage.error(res.message as any)
      return
    }
    ElMessage.success('对话已清空' as any)
  } catch (error) {
    ElMessage.error('清空对话失败' as any)
  }
}

const returnFileList = () => {
  aiReadStore.currentFile = null
}

// 自动滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesEndRef.value) {
      messagesEndRef.value.scrollIntoView({ behavior: 'smooth' })
    }
  })
}

// 监听消息变化，自动滚动
watch(() => aiReadStore.chatMessages.length, scrollToBottom)
watch(() => aiReadStore.isStreaming, scrollToBottom)
watch(() => fileStore.aiReaderTheme, (newTheme) => {
  console.log('Theme changed to:', newTheme)
  darkMode.value = newTheme === 'dark'
}, { immediate: true })

onMounted(() => {
  aiReadStore.loadChatHistory()
})
</script>

<template>
  <div class="ai-reader-container" :class="{ 'dark-mode': darkMode }">
    <div class="main-layout">
      <!-- 左侧聊天区域 -->
      <div class="chat-section">
        <div class="chat-header">
          <div class="header-content">
            <h3>AI 智能阅读器</h3>
            <p class="subtitle">与您的文档进行智能对话</p>
          </div>
          <div class="header-actions">
            <el-tooltip content="返回文件列表" placement="bottom">
              <el-button
                  circle
                  class="download-btn"
                  :icon="DArrowLeft"
                  @click="returnFileList"
              />
            </el-tooltip>
            <el-tooltip :content="showPdf ? '隐藏文件' : '显示文件'" placement="bottom">
              <el-button
                  @click="toggleFile"
                  circle
                  class="toggle-btn"
                  :icon="showPdf ? Hide : View"
              />
            </el-tooltip>
            <el-tooltip content="下载对话记录" placement="bottom">
              <el-button
                  circle
                  class="download-btn"
                  :icon="Download"
                  @click="downloadAiResponse"
              />
            </el-tooltip>
            <el-tooltip content="清空对话记录" placement="bottom">
              <el-button
                  circle
                  class="download-btn"
                  :icon="Delete"
                  @click="clearChat"
              />
            </el-tooltip>
          </div>
          <div class="pdf-header">
            <div class="pdf-info" v-if="aiReadStore.currentFile">
              <span class="file-name">{{ aiReadStore.currentFile.title }}</span>
              <span class="file-size">{{ formatFileSize(aiReadStore.currentFile.fileSize) }}</span>
            </div>
          </div>
        </div>

        <div class="chat-messages">
          <div
              v-for="message in aiReadStore.chatMessages"
              :key="message.id"
              class="message-item"
              :class="message.type.toLowerCase()"
          >
            <div class="message-avatar">
              <el-avatar
                  v-if="message.type === 'USER'"
                  :icon="UserFilled"
                  :style="{ backgroundColor: '#3b82f6' }"
              />
              <el-avatar
                  v-else-if="message.type === 'ASSISTANT'"
                  :icon="ChatDotRound"
                  :style="{ backgroundColor: '#10b981' }"
              />
              <el-avatar
                  v-else
                  :icon="WarningFilled"
                  :style="{ backgroundColor: '#6b7280' }"
              />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-role">{{ getRoleName(message.type) }}</span>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
                <el-tooltip
                    content="删除"
                    placement="top"
                    :show-after="300"
                >
                  <el-button
                      v-if="message.type !== 'SYSTEM'"
                      size="small"
                      :icon="Delete"
                      @click.stop="handleDeleteMessage(message.id)"
                      :disabled="aiReadStore.isLoading"
                      circle
                      class="delete-btn"
                  />
                </el-tooltip>
              </div>
              <div class="message-text" v-html="markDownIt.render(message.content)"></div>
            </div>
          </div>

          <!-- 加载指示器 -->
          <div v-if="aiReadStore.isStreaming" class="message-item assistant">
            <div class="message-avatar">
              <el-avatar :icon="ChatDotRound" :style="{ backgroundColor: '#10b981' }" />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-role">AI助手</span>
                <span class="message-time">正在输入...</span>
              </div>
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>

          <div ref="messagesEndRef"></div>
        </div>

        <div class="chat-input">
          <el-input
              v-model="userInput"
              placeholder="请输入您的问题..."
              @keyup.enter="sendMessage"
              class="input-box"
              :disabled="aiReadStore.isLoading"
              resize="none"
              type="textarea"
              :rows="3"
          >
            <template #append>
              <el-button
                  type="primary"
                  @click="sendMessage"
                  :loading="aiReadStore.isLoading"
                  class="send-btn"
                  :disabled="!userInput.trim()"
              >
                发送
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 右侧PDF区域 -->
      <el-collapse-transition>
        <div class="pdf-section" v-show="showPdf">
          <div class="pdf-container">
            <div v-if="aiReadStore.currentFile?.url" class="pdf-viewer">
              <iframe
                  :src="aiReadStore.currentFile.url"
                  class="pdf-iframe"
                  frameborder="0"
              ></iframe>
            </div>
            <div v-else class="empty-state">
              <div class="empty-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M14 2V8H20" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M16 13H8" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M16 17H8" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 9H9H8" stroke="#9ca3af" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <p class="empty-title">暂无文件预览</p>
              <p class="empty-hint">请上传文件开始对话</p>
            </div>
          </div>
        </div>
      </el-collapse-transition>
    </div>
  </div>
</template>

<style scoped>
.ai-reader-container {
  height: 100vh;
  overflow: hidden;
  transition: all 0.3s ease;
  background: transparent;
}

.main-layout {
  display: flex;
  height: 100%;
  max-width: 1600px;
  margin: 0 auto;
  background: rgba(251, 249, 255, 0.85);
  backdrop-filter: blur(10px);
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.15);
  transition: all 0.3s ease;
}

/* 暗色模式样式 - 与Background.vue配色匹配 */
.ai-reader-container.dark-mode .main-layout {
  background: rgba(44, 62, 80, 0.85);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}

/* 左侧聊天区域 */
.chat-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .chat-section {
  border-right-color: rgba(200, 220, 255, 0.2);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: rgba(245, 243, 255, 0.9);
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  box-shadow: 0 1px 3px rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .chat-header {
  background: rgba(52, 73, 94, 0.9);
  border-bottom-color: rgba(200, 220, 255, 0.2);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.header-content h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.ai-reader-container.dark-mode .header-content h3 {
  background: linear-gradient(135deg, #a29bfe 0%, #6c5ce7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  margin: 4px 0 0 0;
  color: #667eea;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.ai-reader-container.dark-mode .subtitle {
  color: #a29bfe;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.toggle-btn,
.download-btn {
  border: 1px solid rgba(102, 126, 234, 0.3);
  background: rgba(245, 243, 255, 0.8);
  color: #667eea;
  transition: all 0.2s ease;
}

.ai-reader-container.dark-mode .toggle-btn,
.ai-reader-container.dark-mode .download-btn {
  border-color: rgba(200, 220, 255, 0.3);
  background: rgba(52, 73, 94, 0.8);
  color: rgba(255, 255, 255, 0.9);
}

.toggle-btn:hover,
.download-btn:hover {
  border-color: #764ba2;
  background: rgba(255, 255, 255, 0.9);
  color: #764ba2;
  transform: translateY(-1px);
}

.ai-reader-container.dark-mode .toggle-btn:hover,
.ai-reader-container.dark-mode .download-btn:hover {
  border-color: #6c5ce7;
  background: rgba(44, 62, 80, 0.9);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(249, 247, 255, 0.8);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .chat-messages {
  background: rgba(44, 62, 80, 0.8);
}

.message-item {
  display: flex;
  margin-bottom: 24px;
  animation: slideIn 0.3s ease;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  margin: 0 12px;
}

.message-content {
  max-width: 70%;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.15);
  position: relative;
  transition: all 0.3s ease;
  color: #4a4a68;
}

.ai-reader-container.dark-mode .message-content {
  background: rgba(52, 73, 94, 0.95);
  box-shadow: 0 2px 12px rgba(200, 220, 255, 0.15);
  color: rgba(255, 255, 255, 0.9);
}

.message-item.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.ai-reader-container.dark-mode .message-item.user .message-content {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
}

.message-item.assistant .message-content {
  border-bottom-left-radius: 6px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 0.8rem;
}

.message-role {
  font-weight: 600;
  color: inherit;
  opacity: 0.8;
}

.message-time {
  color: inherit;
  opacity: 0.6;
}

.delete-btn {
  margin-left: auto;
  opacity: 0.6;
  transition: opacity 0.2s ease;
  color: inherit;
}

.delete-btn:hover {
  opacity: 1;
}

.message-text {
  line-height: 1.6;
  color: inherit;
}

.message-text :deep(*) {
  margin: 0;
  line-height: 1.6;
}

.message-text :deep(p) {
  margin-bottom: 8px;
}

.message-text :deep(code) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Monaco', monospace;
}

.ai-reader-container.dark-mode .message-text :deep(code) {
  background: rgba(200, 220, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

.message-item.user .message-text :deep(code) {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.ai-reader-container.dark-mode .typing-indicator span {
  background: #a29bfe;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
  background: rgba(245, 243, 255, 0.9);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .chat-input {
  background: rgba(52, 73, 94, 0.9);
  border-top-color: rgba(200, 220, 255, 0.2);
}

.input-box :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  resize: none;
  font-family: inherit;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.95);
  color: #4a4a68;
}

.ai-reader-container.dark-mode .input-box :deep(.el-textarea__inner) {
  background: rgba(52, 73, 94, 0.95);
  border-color: rgba(200, 220, 255, 0.3);
  color: rgba(255, 255, 255, 0.9);
}

.input-box :deep(.el-input-group__append) {
  background: transparent;
  border: none;
}

.send-btn {
  height: 100%;
  border-radius: 0 12px 12px 0;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.ai-reader-container.dark-mode .send-btn {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
}

/* 右侧PDF区域 */
.pdf-section {
  width: 45%;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  background: rgba(245, 243, 255, 0.9);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .pdf-section {
  background: rgba(44, 62, 80, 0.9);
}

.pdf-header {
  padding: 20px 24px;
  background: rgba(240, 237, 255, 0.9);
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.ai-reader-container.dark-mode .pdf-header {
  background: rgba(52, 73, 94, 0.9);
  border-bottom-color: rgba(200, 220, 255, 0.2);
}

.pdf-header h3 {
  margin: 0 0 12px 0;
  font-size: 1.2rem;
  color: #667eea;
  font-weight: 600;
  transition: color 0.3s ease;
}

.ai-reader-container.dark-mode .pdf-header h3 {
  color: #a29bfe;
}

.pdf-info {
  display: flex;
  gap: 4px;
}

.file-name {
  font-weight: 500;
  color: #667eea;
  height: 15px;
  line-height: 15px;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.ai-reader-container.dark-mode .file-name {
  color: #a29bfe;
}

.file-size {
  height: 15px;
  line-height: 15px;
  color: #764ba2;
  font-size: 0.8rem;
  transition: color 0.3s ease;
}

.ai-reader-container.dark-mode .file-size {
  color: #6c5ce7;
}

.pdf-container {
  flex: 1;
  overflow: hidden;
}

.pdf-viewer {
  height: 100%;
  background: rgba(240, 237, 255, 0.9);
  transition: background 0.3s ease;
}

.ai-reader-container.dark-mode .pdf-viewer {
  background: rgba(40, 56, 72, 0.9);
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px;
  color: #667eea;
  transition: color 0.3s ease;
}

.ai-reader-container.dark-mode .empty-state {
  color: #a29bfe;
}

.empty-icon {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-title {
  margin: 0 0 8px 0;
  font-weight: 600;
  font-size: 1.1rem;
}

.empty-hint {
  margin: 0;
  font-size: 0.9rem;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-layout {
    flex-direction: column;
  }

  .pdf-section {
    width: 100%;
    min-width: unset;
    height: 40%;
  }

  .chat-section {
    border-right: none;
    border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  }

  .ai-reader-container.dark-mode .chat-section {
    border-bottom-color: rgba(200, 220, 255, 0.2);
  }
}

@media (max-width: 768px) {
  .chat-header {
    padding: 16px;
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .header-actions {
    justify-content: center;
  }

  .chat-messages {
    padding: 16px;
  }

  .message-content {
    max-width: 85%;
  }

  .chat-input {
    padding: 16px;
  }

  .pdf-header {
    padding: 16px;
  }

  .empty-state {
    padding: 24px;
  }
}

/* 动画 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

</style>