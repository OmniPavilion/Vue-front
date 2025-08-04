<template>
  <div class="markdown-editor-container">
    <!-- 顶部操作栏 -->
    <div class="editor-toolbar">
      <el-button-group>
        <el-button
            type="primary"
            plain
            @click="handOpenCreateDialog"
        >
          <el-icon>
            <DocumentAdd/>
          </el-icon>
          新建
        </el-button>
        <el-button
            type="primary"
            plain
            @click="handOpenUpdateDialog"
        >
          <el-icon>
            <Edit/>
          </el-icon>
          编辑
        </el-button>
        <el-button
            type="primary"
            @click="handleSave"
            :disabled="fileStore.isSave || !fileStore.currentArticle"
        >
          <el-icon>
            <Check/>
          </el-icon>
          保存
        </el-button>
        <el-button @click="togglePreview">
          <el-icon>
            <View v-if="showPreview"/>
            <Hide v-else/>
          </el-icon>
        </el-button>

      </el-button-group>

      <div class="file-info">
        <span v-if="fileStore.currentArticle">
          {{ fileStore.currentArticle.title }}
          <el-tag v-if="!fileStore.isSave" type="warning" size="small">未保存</el-tag>
        </span>
        <span v-else class="empty-tip">未选择文章</span>
      </div>
    </div>

    <!-- 新建文章对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        :title="isCreating ? '新建文章' : '保存文章'"
        width="500px"
        @close="resetForm"
    >
      <el-form
          ref="articleFormRef"
          :model="articleForm"
          :rules="articleRules"
          label-width="80px"
      >
        <el-form-item label="文章标题" prop="title">
          <el-input
              v-model="articleForm.title"
              placeholder="请输入文章标题"
              clearable
          />
        </el-form-item>
        <el-form-item label="文章分类">
          <el-select
              v-model="articleForm.tagIds"
              placeholder="请选择分类"
              clearable
              multiple
              style="width: 100%"
          >
            <el-option
                v-for="tag in tagStore.allTags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="天气" prop="weather">
          <el-input
              v-model="articleForm.weather"
              placeholder="请输入天气情况"
              clearable
          />
        </el-form-item>
        <el-form-item label="日期" prop="writtenAt">
          <el-date-picker
              v-model="articleForm.writtenAt"
              type="datetime"
              placeholder="选择日期和时间"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="!isCreating" type="danger" @click="handleDeleteArticle">删除</el-button>
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCreateArticle">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑与预览区域 -->
    <div class="editor-preview-container">
      <!-- 编辑区域 -->
      <div class="editor-area" :style="{ width: editorWidth }">
        <el-input
            v-model="fileStore.fileContent"
            type="textarea"
            resize="none"
            class="full-height-textarea"
            placeholder="请输入Markdown内容..."
            @input="handleContentChange"
        />
      </div>

      <!-- 预览区域 -->
      <div
          class="preview-area"
          v-show="showPreview"
          :style="{ width: previewWidth }"
          v-html="compiledHtml"
      />
    </div>

    <!-- 新增底部状态栏 -->
    <div class="status-bar" v-if="fileStore.currentArticle">
      <div class="status-item">
        <el-icon>
          <Calendar/>
        </el-icon>
        <span>{{ formatDateTime(fileStore.currentArticle.writtenAt) }}</span>
      </div>
      <div class="status-item">
        <el-icon>
          <Sunny/>
        </el-icon>
        <span>{{ fileStore.currentArticle.weather || '无天气信息' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, computed, watchEffect, onMounted, onUnmounted} from 'vue'
import {Check, View, Hide, DocumentAdd, Sunny, Calendar, Edit} from '@element-plus/icons-vue'
import {useArticleFileStore} from '@/article/stores'
import {useArticleStore} from '@/article/stores'
import {useTagStore} from '@/article/stores'
import {marked} from 'marked'
import DOMPurify from 'dompurify'
import {ElMessage, ElMessageBox, type FormInstance, type FormRules} from 'element-plus'
import type {ArticleVO} from '@/article/types/vo/ArticleVO'

const fileStore = useArticleFileStore()
const articleStore = useArticleStore()
const tagStore = useTagStore()
const compiledHtml = ref('<p>加载中...</p>')
const showPreview = ref(true)

// 配置 marked
marked.setOptions({
  async: false,
  gfm: true,
  breaks: true,
  pedantic: false
})

// 计算编辑器宽度
const editorWidth = computed(() => showPreview.value ? '50%' : '100%')
const previewWidth = computed(() => showPreview.value ? '50%' : '0')

// 切换预览显示状态
const togglePreview = () => {
  showPreview.value = !showPreview.value
}

// 实时编译 Markdown
watchEffect(() => {
  if (!fileStore.fileContent) {
    compiledHtml.value = '<p>暂无内容</p>'
    return
  }

  try {
    const rawHtml = marked(fileStore.fileContent)
    compiledHtml.value = DOMPurify.sanitize(rawHtml)
  } catch (error) {
    console.error('Markdown解析错误:', error)
    compiledHtml.value = '<p>内容解析错误</p>'
  }
})

// 内容变化处理
const handleContentChange = () => {
  fileStore.isSave = false
}

// 保存文章
const handleSave = async () => {
  if (!fileStore.currentArticle?.id) {
    ElMessage.warning('请选择文章' as any)
    return
  }

  try {
    await fileStore.updateArticleFile(
        fileStore.currentArticle.id,
        fileStore.fileContent
    )
    fileStore.isSave = true
    ElMessage.success('保存成功' as any)
  } catch (error) {
    ElMessage.error('保存失败' as any)
  }
}

// 新建文章对话框相关
const handOpenCreateDialog = () => {
  articleForm.value = {
    title: '',
    weather: '',
    writtenAt: new Date().toISOString(),
    tagIds: [],
  }
  showCreateDialog.value = true
  isCreating.value = true
}

const handOpenUpdateDialog = () => {
  if (!fileStore.currentArticle) {
    ElMessage.warning("请先选择文章" as any)
  }
  articleForm.value = {
    title: fileStore.currentArticle!.title,
    weather: fileStore.currentArticle!.weather,
    writtenAt: fileStore.currentArticle!.writtenAt,
    tagIds: fileStore.currentArticle!.tagIds,
  }
  showCreateDialog.value = true
  isCreating.value = false
}

const isCreating = ref(false)
const showCreateDialog = ref(false)
const articleFormRef = ref<FormInstance>()
const articleForm = ref<Omit<ArticleVO, 'id' | 'fileName'>>({
  title: '',
  weather: '',
  writtenAt: new Date().toISOString(),
  tagIds: []
})

const articleRules = reactive<FormRules>({
  title: [
    {required: true, message: '请输入文章标题', trigger: 'blur'},
    {min: 1, max: 50, message: '长度在1到50个字符', trigger: 'blur'}
  ],
  weather: [
    {required: true, message: '请输入天气情况', trigger: 'blur'},
    {min: 1, max: 20, message: '长度在1到20个字符', trigger: 'blur'}
  ],
  writtenAt: [
    {required: true, message: '请选择日期', trigger: 'change'}
  ]
})

// 重置表单
const resetForm = () => {
  articleFormRef.value?.resetFields()
  articleForm.value.writtenAt = new Date().toISOString()
}

// 删除文章
const handleDeleteArticle = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      distinguishCancelAndClose: true // 区分取消和关闭操作
    })

    // 用户确认删除
    await articleStore.deleteArticle(fileStore.currentArticle!.id!)
    ElMessage.success('删除成功' as any)

    fileStore.currentArticle = null
    fileStore.fileContent = ''

    showCreateDialog.value = false
  } catch (error) {
    // 用户取消删除
    if (error === 'cancel') {
      ElMessage.info('已取消删除' as any)
    } else {
      // 其他错误
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试' as any)
    }
  }
}

// 创建新文章
const handleCreateArticle = async () => {
  if (!articleFormRef.value) return

  try {
    await articleFormRef.value.validate()

    const newArticle: ArticleVO = {
      ...articleForm.value,
      fileName: `${Date.now()}.md`, // 生成唯一文件名
    }

    var res
    if (isCreating) {
      res = await articleStore.createArticle(newArticle)
    } else {
      res = await articleStore.updateArticle(newArticle)
    }
    if (res.code === 1) {
      if (isCreating) {
        newArticle.id = res.data!
        fileStore.fileContent = ''
      }

      fileStore.currentArticle = newArticle
      fileStore.isSave = true
      showCreateDialog.value = false
      ElMessage.success(isCreating ? '新建文章成功' : '保存成功' as any)
    }
  } catch (error) {
    console.error('创建文章失败:', error)
  }
}

// 新增日期格式化方法
const formatDateTime = (isoString: string) => {
  if (!isoString) return '无日期信息'
  const date = new Date(isoString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 快捷键保存功能
const handleKeyDown = (e: KeyboardEvent) => {
  // 检查是否按下 Ctrl/Cmd + S
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault() // 阻止浏览器默认保存行为
    handleSave() // 调用保存方法
  }
}

// 添加和移除键盘事件监听
onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style scoped lang="scss">
@import "@/article/styles/element/index.scss";

.markdown-editor-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  margin: auto;
  max-width: 800px;
  background-color: rgba($notebook-bg, 0.95);
  border: $border-width $border-style $border-color;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.05);
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: $border-width $border-style $border-color;
  background-color: rgba($notebook-edge, 0.1);

  .file-info {
    font-size: 14px;
    color: $text-primary;

    .empty-tip {
      color: $text-light;
      font-style: italic;
    }
  }
}

.editor-preview-container {
  display: flex;
  flex: 1;
  overflow: hidden;
  padding: 15px;
  gap: 15px;

  .editor-area, .preview-area {
    height: 100%;
    overflow: auto;
    background-color: rgba($notebook-bg, 0.9);
    border: $border-width $border-style $border-color;
    padding: 10px;
    transition: width 0.3s ease;
  }

  .preview-area {
    flex-shrink: 0;
  }
}

:deep(.full-height-textarea) {
  height: 100%;

  .el-textarea__inner {
    height: 100% !important;
    min-height: 100% !important;
    resize: none;
    font-family: "Courier New", monospace;
    line-height: 1.6;
    background-color: rgba($notebook-bg, 0.9);
    color: $text-primary;
    border: none;
    box-shadow: none;

    &:focus {
      box-shadow: none;
    }
  }
}

.preview-area {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  line-height: 1.6;
  color: $text-primary;

  :deep() {
    h1, h2, h3, h4, h5, h6 {
      margin: 1em 0 0.5em;
      color: $primary-color;
      border-bottom: 1px solid rgba($notebook-edge, 0.2);
    }

    p {
      margin: 0 0 1em;
    }

    pre {
      background-color: rgba($notebook-edge, 0.1);
      padding: 10px;
      border-radius: 3px;
      overflow: auto;
    }

    code {
      font-family: "Courier New", monospace;
      background-color: rgba($notebook-edge, 0.1);
      padding: 2px 4px;
      border-radius: 3px;
    }

    blockquote {
      border-left: 3px solid $primary-color;
      padding-left: 10px;
      margin-left: 0;
      color: $text-secondary;
    }

    a {
      color: $info-color;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }

    /* 修复列表样式 */
    ol, ul {
      padding-left: 2em;
      list-style-position: outside;
      margin-left: 0;
      overflow: visible;
    }

    li {
      margin-left: 1em;
      white-space: pre-wrap;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.status-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  padding: 8px 15px;
  background-color: rgba($notebook-edge, 0.1);
  border-top: $border-width $border-style $border-color;
  color: $text-secondary;
  font-size: 12px;

  .status-item {
    display: flex;
    align-items: center;
    gap: 6px;

    .el-icon {
      font-size: 14px;
    }
  }
}
</style>