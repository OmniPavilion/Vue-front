<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue';
import {useVocabularyStore} from '@/english/stores';
import type {Vocabulary} from "@/english/types/vo/detailWord/Vocabulary";
import type {Detail} from "@/english/types/vo/detailWord/Detail";
import {ElMessage, ElMessageBox} from "element-plus";
import { Plus } from "@element-plus/icons-vue";

// 使用Pinia store
const vocabularyStore = useVocabularyStore();

const isDialogShow = ref(false)
const isUpdate = ref(false)

// 计算属性获取单词列表和加载状态
const vocabularies = computed(() => vocabularyStore.vocabularies);
const loading = computed(() => vocabularyStore.loading);

// 表单引用和表单数据
const wordFormRef = ref()
const formData = ref<Vocabulary>({
  status: 1,
  english: '',
  phonetic: '',
  details: [{
    partOfSpeech: '',
    partOfSpeechId: 0,
    meaning: '',
    sentence: '',
    translation: ''
  }]
})

// 词性选项（示例数据，实际应从store获取）
const partOfSpeechOptions = ref<Map<number, string>>(new Map());

// 状态选项
const statusOptions = ref<Map<number, string>>(new Map());

// 表单验证规则
const rules = {
  english: [
    { required: true, message: '请输入英文单词', trigger: 'blur' }
  ],
  phonetic: [
    { required: true, message: '请输入音标', trigger: 'blur' }
  ],
  details: [
    {
      validator: (_: any, value: Detail[], callback: any) => {
        if (!value || value.length === 0) {
          callback(new Error('至少需要一个释义'));
        } else if (!value[0].meaning) {
          callback(new Error('请输入中文释义'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
}

// 监听对话框显示/隐藏
watch(isDialogShow, (val) => {
  if (!val) {
    // 对话框关闭时重置表单
    resetForm()
  }
})

// 重置表单
const resetForm = () => {
  formData.value = {
    status: 1,
    english: '',
    phonetic: '',
    details: [{
      partOfSpeech: '',
      partOfSpeechId: 0,
      meaning: '',
      sentence: '',
      translation: ''
    }]
  }
  wordFormRef.value?.clearValidate()
}

// 添加释义
const addDetail = () => {
  formData.value.details?.push({
    partOfSpeech: '',
    partOfSpeechId: 0,
    meaning: '',
    sentence: '',
    translation: ''
  })
}

// 删除释义
const removeDetail = (index: number) => {
  if (formData.value.details && formData.value.details.length > 1) {
    formData.value.details.splice(index, 1)
  } else {
    ElMessage.warning('至少需要保留一个释义' as any)
  }
}

// 显示单词详情
const showWordDetail = (vocabulary: Vocabulary) => {
  vocabularyStore.currentVocabulary = vocabulary
  formData.value = {...vocabulary}
  isDialogShow.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!wordFormRef.value) return
  console.log('提交表单', formData.value)

  try {
    const valid = await wordFormRef.value.validate()
    if (valid) {
      if (isUpdate.value) {
        // 更新单词
        const res = await vocabularyStore.updateVocabulary(formData.value)
        if (res.code === 1) {
          ElMessage.success('单词更新成功' as any)
          isDialogShow.value = false
          await vocabularyStore.fetchVocabularyPage()
        } else {
          ElMessage.error(res.message || '更新失败' as any)
        }
      } else {
        // 添加单词
        const res = await vocabularyStore.createVocabulary(formData.value)
        if (res.code === 1) {
          ElMessage.success('单词添加成功' as any)
          isDialogShow.value = false
          await vocabularyStore.fetchVocabularyPage()
        } else {
          ElMessage.error(res.message || '添加失败' as any)
        }
      }
    }
  } catch (error) {
    console.error('表单验证失败', error)
  }
}

// 删除单词
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个单词吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await vocabularyStore.deleteVocabulary(id)
    if (res.code === 1) {
      ElMessage.success('删除成功' as any)
      isDialogShow.value = false
      await vocabularyStore.fetchVocabularyPage()
    } else {
      ElMessage.error(res.message || '删除失败' as any)
    }
  } catch (error) {
    // 用户取消删除
    console.log('取消删除')
  }
}

const changeStatus = async (vocabulary: Vocabulary) => {
  if (vocabulary.status === undefined || vocabulary.id == null) {
    ElMessage.error('单词状态不能为空' as any)
    return
  }

  const status = vocabulary.status % 3 + 1;

  const res = await vocabularyStore.updateVocabularyStatus(status, vocabulary.id)
  if (res.code === -1) {
    ElMessage.error(res.message as any)
    return
  } else {
    ElMessage.success('单词状态已更新' as any)
  }
};

onMounted(async () => {
  const res01 = await vocabularyStore.fetchPartOfSpeechDict()
  if (res01.code === 1) {
    partOfSpeechOptions.value = res01.data
  } else {
    ElMessage.error(res01.message || '获取词性失败' as any)
  }

  const res02 = await vocabularyStore.fetchStatusDict()
  if (res02.code === 1) {
    statusOptions.value = res02.data
  } else {
    ElMessage.error(res02.message || '获取状态失败' as any)
  }
})
</script>

<template>
  <div class="word-table-container">
    <el-table
        :data="vocabularies"
        v-loading="loading"
        style="width: 100%; height: 700px;"
        :default-sort="{ prop: 'id', order: 'descending' }"
        stripe
    >
      <el-table-column
          label="序号"
          width="80"
          type="index"
          :index="(index: number) => index + 1"
      />
      <el-table-column
          prop="english"
          label="英文"
          min-width="120"
          sortable
      />
      <el-table-column
          label="中文"
          min-width="120"
          sortable
      >
        <template #default="{ row }">
          {{ row.details && row.details[0] ? row.details[0].meaning : '--' }}
        </template>
      </el-table-column>
      <el-table-column
          prop="phonetic"
          label="音标"
          min-width="150"
      >
        <template #default="{ row }">
          <span v-if="row.phonetic">/{{ row.phonetic }}/</span>
          <span v-else class="no-phonetic">---</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="150" flex-grow="2" sortable>
        <template #default="{ row }">
          <el-tag
              :type="row.status === 1 ? 'info' : (row.status === 2 ? 'danger' : 'success')"
              @click="changeStatus(row)"
              style="cursor: pointer;user-select: none"
          >
            {{ row.status === 1 ? '默认' : (row.status === 2 ? '重点' : '已学习') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="120">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="showWordDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 单词详情对话框 -->
    <el-dialog
        v-model="isDialogShow"
        width="800px"
        :close-on-click-modal="false"
        append-to-body
    >
      <template #title>
        <span style="margin-right: 10px; font-size: 25px; font-weight: bold;">单词详情</span>
        <el-tag
            :type="isUpdate ? 'danger' : 'success'"
            @click="isUpdate = !isUpdate"
            style="cursor: pointer;user-select: none"
        >
          {{ isUpdate ? '取消' : '编辑' }}
        </el-tag>
      </template>

      <el-form
          ref="wordFormRef"
          :model="formData"
          :rules="isUpdate ? rules : {}"
          label-width="80px"
          label-position="left"
          :disabled="!isUpdate"
      >
        <el-form-item label="英文" prop="english">
          <el-input v-model="formData.english" placeholder="请输入英文单词" />
        </el-form-item>

        <el-form-item label="音标" prop="phonetic">
          <el-input v-model="formData.phonetic" placeholder="请输入音标">
            <template #prepend>/</template>
            <template #append>/</template>
          </el-input>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option
                v-for="(item, key) in statusOptions"
                :key="item"
                :label="item"
                :value="Number(key)"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="释义" prop="details">
          <div class="details-container">
            <div v-for="(detail, index) in formData.details" :key="index" class="detail-item">
              <div class="detail-header">
                <span>释义 {{ index + 1 }}</span>
                <el-button
                    v-if="isUpdate && formData.details && formData.details.length > 1"
                    type="danger"
                    size="small"
                    link
                    @click="removeDetail(index)"
                >
                  删除
                </el-button>
              </div>

              <div class="detail-content">
                <el-form-item
                    :prop="`details[${index}].partOfSpeechId`"
                    :rules="{ required: true, message: '请选择词性', trigger: 'change' }"
                    label="词性"
                    class="inline-form-item"
                >
                  <el-select
                      v-model="detail.partOfSpeechId"
                      placeholder="请选择词性"
                  >
                    <el-option
                        v-for="(item, key) in partOfSpeechOptions"
                        :key="item"
                        :label="item"
                        :value="Number(key)"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item
                    :prop="`details[${index}].meaning`"
                    :rules="isUpdate ? { required: true, message: '请输入中文释义', trigger: 'blur' } : {}"
                    label="中文释义"
                    class="inline-form-item"
                >
                  <el-input v-model="detail.meaning" placeholder="请输入中文释义" />
                </el-form-item>

                <el-form-item
                    :prop="`details[${index}].sentence`"
                    label="例句"
                    class="inline-form-item"
                >
                  <el-input
                      v-model="detail.sentence"
                      placeholder="请输入例句"
                      type="textarea"
                      :rows="2"
                  />
                </el-form-item>

                <el-form-item
                    :prop="`details[${index}].translation`"
                    label="例句翻译"
                    class="inline-form-item"
                >
                  <el-input
                      v-model="detail.translation"
                      placeholder="请输入例句翻译"
                      type="textarea"
                      :rows="2"
                  />
                </el-form-item>
              </div>
            </div>

            <el-button v-if="isUpdate" type="primary" link @click="addDetail">
              <el-icon><Plus /></el-icon>
              添加释义
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
    <span class="dialog-footer">
      <el-button
          v-if="formData.id"
          type="danger"
          @click="handleDelete(formData.id)"
      >
        删除
      </el-button>
      <el-button @click="isDialogShow = false">关闭</el-button>
      <el-button
          v-if="isUpdate"
          type="primary"
          @click="submitForm"
      >
        更新
      </el-button>
    </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.word-table-container {
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  transition: box-shadow 0.3s ease;
}

.word-table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.word-table-container :deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

.word-table-container :deep(.el-table__header) {
  background: linear-gradient(135deg, #f8f9fc 0%, #e9ecef 100%);
}

.word-table-container :deep(.el-table th) {
  background: transparent;
  color: #2c3e50;
  font-weight: 600;
  height: 52px;
  border-bottom: 2px solid #e8eff8;
}

.word-table-container :deep(.el-table td) {
  padding: 16px 0;
  border-bottom: 1px solid #f0f4f8;
  transition: background-color 0.2s ease;
}

.word-table-container :deep(.el-table tr:hover td) {
  background-color: #f8fbff !important;
}

.word-table-container :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafcff;
}

.word-table-container :deep(.el-table .cell) {
  padding: 0 16px;
  font-size: 14px;
  color: #34495e;
}

.word-table-container :deep(.el-table .no-phonetic) {
  color: #a0a8b8;
  font-style: italic;
  font-size: 13px;
}

.word-table-container :deep(.el-loading-mask) {
  border-radius: 8px;
  background-color: rgba(255, 255, 255, 0.9);
}

/* 对话框样式 */
.details-container {
  max-height: 500px;
  overflow: auto;
  width: 100%;
}

.details-container::-webkit-scrollbar {
  display: none;
}

.detail-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e8f4ff;
  border-radius: 8px;
  background-color: #f8fbff;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #dcdfe6;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.inline-form-item {
  margin-bottom: 0;
}

.inline-form-item :deep(.el-form-item__label) {
  width: 80px;
}

.inline-form-item :deep(.el-form-item__content) {
  flex: 1;
}
</style>