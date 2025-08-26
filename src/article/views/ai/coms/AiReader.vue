<template>
  <div class="ai-chat-container">
    <!-- 聊天消息区域 -->
    <div ref="messagesContainer" class="chat-messages">
      <div v-for="message in readStore.chatMessages" :key="message.id" class="message-item" :class="message.type">
        <div class="message-avatar">
          <el-avatar v-if="message.type === 'USER'" :icon="UserFilled"/>
          <el-avatar v-else-if="message.type === 'ASSISTANT'" :icon="ChatDotRound"/>
          <el-avatar v-else :icon="WarningFilled"/>
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="message-role">{{ getRoleName(message.type) }}</span>
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
            <el-tooltip
                content="删除"
                placement="top"
                :show-after="300"
                effect="dark"
            >
              <el-button
                  v-if="message.type !== 'SYSTEM'"
                  size="small"
                  style="border: none !important;"
                  :icon="Delete"
                  @click.stop="handleDeleteMessage(message.id)"
                  :disabled="readStore.isLoading"

                  circle
              />
            </el-tooltip>
          </div>
          <div class="message-text" v-html=" markDownIt.render(message.content)"></div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="readStore.isStreaming" class="loading-indicator">
        <el-icon class="is-loading">
          <Loading/>
        </el-icon>
        <span>AI 正在思考...</span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <div class="chat-actions">
        <el-button-group>
          <el-tooltip content="清除对话历史" placement="top">
            <el-button :icon="Delete" @click="clearChat"
                       :disabled="!readStore.currentArticleId || readStore.chatMessages.length === 0 || readStore.isStreaming"/>
          </el-tooltip>
          <el-tooltip content="下载对话记录" placement="top">
            <el-button :icon="Download" @click="downloadChat"
                       :disabled="!readStore.currentArticleId || readStore.chatMessages.length === 0 || readStore.isStreaming"/>
          </el-tooltip>
        </el-button-group>
      </div>
      <div class="input-wrapper">
        <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            :autosize="{ minRows: 3, maxRows: 6 }"
            placeholder="输入您的问题..."
            @keyup.enter.exact="handleSendMessage"
            :disabled="readStore.isStreaming"
        />
        <el-button
            class="send-button"
            type="primary"
            :icon="Promotion"
            @click="handleSendMessage"
            :loading="readStore.isStreaming"
            :disabled="!userInput.trim() || readStore.isStreaming"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, watch, nextTick, onMounted} from 'vue'
import {useReadStore} from '@/article/stores'
import {useArticleFileStore} from '@/article/stores'
import {
  UserFilled,
  ChatDotRound,
  WarningFilled,
  Delete,
  Download,
  Promotion,
  Loading
} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {markDownIt} from "@/common/utils/tools";

const readStore = useReadStore()
const articleFileStore = useArticleFileStore()

const userInput = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

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
  return new Date(timestamp).toLocaleTimeString()
}

// 发送消息
const handleSendMessage = async () => {
  if (!userInput.value.trim() || readStore.isStreaming) return

  const content = userInput.value.trim()
  userInput.value = ''

  try {
    if (!articleFileStore.currentArticle?.id || !articleFileStore.fileContent) {
      ElMessage.warning('请先选择一篇文章' as any)
      return
    }

    await readStore.chatWithArticle(
        articleFileStore.currentArticle.id,
        articleFileStore.fileContent,
        content,
        articleFileStore.currentArticle.title
    )
  } catch (err) {
    ElMessage.error('发送消息失败' as any)
    console.error(err)
  }

  scrollToBottom()
}

// 清除聊天记录
const clearChat = async () => {
  try {
    const res = await readStore.deleteChatHistory()
    if (res.code !== 1) {
      ElMessage.error(res.message as any)
      return
    }
    ElMessage.success('聊天记录已清除' as any)
  } catch (err) {
    ElMessage.error('清除聊天记录失败' as any)
    console.error(err)
  }
}

// 下载聊天记录
const downloadChat = async () => {
  try {
    await readStore.downloadChatHistory()
    ElMessage.success('开始下载聊天记录' as any)
  } catch (err) {
    ElMessage.error('下载聊天记录失败' as any)
    console.error(err)
  }
}

// 删除处理函数
const handleDeleteMessage = async (messageId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await readStore.deleteChatsById(messageId)
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

// 滚动到底部
const scrollToBottom = (behavior: ScrollBehavior = 'smooth') => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTo({
        top: messagesContainer.value.scrollHeight,
        behavior
      });
    }
  });
}

// 组件挂载时
onMounted(() => {
  scrollToBottom('auto'); // 初始加载使用自动滚动，不加动画
});

// 监听消息变化自动滚动
watch(() => readStore.chatMessages, () => {
  scrollToBottom(); // 新消息到达使用平滑滚动
}, {deep: true});

// 监听当前文章变化
watch(() => articleFileStore.currentArticle, (newArticle) => {
  if (newArticle?.id) {
    readStore.loadChatHistory(newArticle.id).then(() => {
      scrollToBottom('auto'); // 切换聊天时使用自动滚动
    });
  }
}, {immediate: true});
</script>

<style scoped lang="scss">
@import "@/article/styles/element/index";

.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  margin: 0 auto;
  max-width: 700px;
  min-width: 400px;
  background-color: rgba($notebook-bg, 0.95);
  border: $border-width $border-style $border-color;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.05);
}

.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-image: linear-gradient(rgba($notebook-grid, 0.1) 1px, transparent 1px);
  background-size: 100% 24px;
}

.message-item {
  display: flex;
  gap: 12px;
  max-width: 90%;

  &.USER {
    align-self: flex-end;
    flex-direction: row-reverse;
  }

  &.system {
    align-self: center;
    max-width: 80%;
    background-color: rgba($notebook-edge, 0.1);
    padding: 8px 12px;
    border: $border-width $border-style $border-color;
  }
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .message-item.USER & {
    align-items: flex-end;
  }
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8rem;
  color: $text-secondary;

  .message-item.USER & {
    flex-direction: row-reverse;
  }
}

.message-text {
  padding: 8px 12px 8px 24px;
  border-radius: 4px;
  background-color: rgba($notebook-bg, 0.9);
  border: $border-width $border-style $border-color;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  word-break: break-word;
  user-select: text;
  line-height: 1.6;

  :deep() {
    h1, h2, h3 {
      color: $primary-color;
      border-bottom: $border-width $border-style rgba($primary-color, 0.3);
      margin: 0.8em 0 0.4em;
    }

    code {
      background-color: rgba($notebook-edge, 0.1);
      padding: 2px 4px;
      border-radius: 2px;
      font-family: 'Courier New', monospace;
    }

    pre {
      background-color: rgba($notebook-edge, 0.1);
      padding: 8px;
      border-radius: 2px;
      border-left: 3px solid $primary-color;
    }

    blockquote {
      border-left: 3px solid $notebook-edge;
      padding-left: 10px;
      color: $text-secondary;
      margin: 0.8em 0;
    }
  }

  .message-item.USER & {
    background-color: rgba($primary-color, 0.1);
    border-color: rgba($primary-color, 0.3);
    color: $text-primary;
  }

  .message-item.ASSISTANT & {
    background-color: rgba($notebook-bg, 0.95);
  }
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  color: $text-secondary;
  font-size: 0.9rem;
  background-color: rgba($notebook-edge, 0.05);
  border: $border-width $border-style $border-color;
  border-radius: 4px;
}

.chat-input-area {
  padding: 12px;
  border-top: $border-width $border-style $border-color;
  background-color: rgba($notebook-bg, 0.98);
}

.chat-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;

  :deep(.el-textarea__inner) {
    background-color: rgba($notebook-bg, 0.95);
    border: $border-width $border-style $border-color;
    color: $text-primary;
    box-shadow: none;

    &:focus {
      border-color: $primary-color;
      box-shadow: 0 0 0 1px rgba($primary-color, 0.3);
    }
  }
}

.send-button {
  position: absolute;
  right: 8px;
  bottom: 8px;
  background-color: $primary-color !important;
  border-color: darken($primary-color, 10%) !important;

  &:hover {
    background-color: lighten($primary-color, 5%) !important;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background-color: rgba($notebook-edge, 0.4);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-track {
  background-color: rgba($notebook-edge, 0.1);
}
</style>