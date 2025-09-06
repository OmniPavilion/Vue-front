<script setup lang="ts">
import { ElTag } from 'element-plus'
import { storeToRefs } from 'pinia'
import { useWordNetStore } from '@/english/stores'
import { Close, DocumentRemove, ArrowLeft } from "@element-plus/icons-vue";

// 使用 Pinia store
const wordNetStore = useWordNetStore()
const { currentNetWords, currentShowNetWord } = storeToRefs(wordNetStore)

// 根据词性获取标签类型
const getTagType = (pos: string) => {
  switch(pos) {
    case 'noun':
      return 'success'; // 名词用绿色
    case 'verb':
      return 'info';    // 动词用蓝色
    case 'adjective':
      return 'warning'; // 形容词用橙色
    case 'adverb':
      return 'danger';  // 副词用红色
    default:
      return 'primary'; // 默认用蓝色
  }
}

// 处理标签点击
const handleTagClick = (wordObj: any) => {
  wordNetStore.currentShowNetWord = wordObj
}

const handleReturn = () => {
  wordNetStore.currentShowNetWord = null
}

const handleClearAll = () => {
  wordNetStore.clearCurrentNetWord()
}

// 处理标签关闭/删除
const handleClose = async (wordObj: any) => {
  wordNetStore.clearCurrentNetWordByWord(wordObj.word)
}
</script>

<template>
  <div class="word-tags-container">
    <!-- 优化后的返回按钮 -->
    <div class="return-section">
      <el-button
          type="primary"
          link
          class="return-btn"
          @click="handleReturn"
          v-if="currentShowNetWord"
      >
        <el-icon><ArrowLeft /></el-icon>
        <span>返回单词列表</span>
      </el-button>

      <el-button
          type="primary"
          link
          class="clear-all-btn"
          @click="handleClearAll"
      >
        <el-icon><DocumentRemove /></el-icon>
        <span>清空所有</span>
      </el-button>
    </div>

    <div class="tags-wrapper">
      <el-tag
          v-for="wordObj in currentNetWords"
          :key="wordObj.word"
          :type="getTagType(wordObj.pos[0])"
          :effect="currentShowNetWord?.word === wordObj.word ? 'dark' : 'plain'"
          class="word-tag"
          @click="handleTagClick(wordObj)"
      >
        {{ wordObj.word }}
        <el-button
            circle
            class="close-btn"
            @click.stop="handleClose(wordObj)"
        >
          <el-icon><Close /></el-icon>
        </el-button>
      </el-tag>
    </div>
  </div>
</template>

<style scoped lang="scss">
.word-tags-container {
  padding: 16px;
  height: 100%;
  border-radius: 8px;
  min-height: 80px;
  position: relative;
}

// 返回按钮区域样式
.return-section {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;

  .return-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 12px;
    color: #409eff;
    font-weight: 500;
    transition: all 0.3s ease;
    border-radius: 4px;

    &:hover {
      color: #66b1ff;
      background-color: rgba(64, 158, 255, 0.1);
      transform: translateX(-2px);
    }

    .el-icon {
      font-size: 14px;
    }

    span {
      font-size: 13px;
    }
  }
}

.empty-tips {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  padding: 20px 0;

  .el-icon {
    font-size: 24px;
    margin-bottom: 8px;
    color: #c0c4cc;
  }
}

.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.word-tag {
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  // 选中状态的样式
  &.el-tag--dark {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }
}

.pos-label {
  font-size: 0.75em;
  opacity: 0.8;
  margin-left: 4px;
  font-weight: normal;
}

.word-tag:hover .close-btn {
  display: block;
}

.close-btn {
  display: none;
  position: absolute;
  width: 16px;
  height: 16px;
  min-width: auto;
  padding: 0;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  color: #606266;
  transition: all 0.2s ease;
  top: -6px;
  right: -6px;

  &:hover {
    background: rgba(255, 255, 255, 0.38);
    color: red;
    transform: scale(1.1);
  }

  .el-icon {
    font-size: 10px;
    font-weight: bold;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .word-tags-container {
    padding: 12px;
  }

  .return-section {
    margin-bottom: 12px;
    padding-bottom: 8px;

    .return-btn {
      padding: 4px 8px;

      span {
        font-size: 12px;
      }
    }
  }

  .word-tag {
    padding: 6px 12px;
    font-size: 13px;
  }

  .tags-wrapper {
    gap: 8px;
  }

  .close-btn {
    width: 14px;
    height: 14px;
    top: -5px;
    right: -5px;

    .el-icon {
      font-size: 9px;
    }
  }
}
</style>

<style>
.word-tag-confirm-dialog {
  border-radius: 12px;
}
</style>