<script setup lang="ts">
import {computed, defineProps, onMounted, ref} from 'vue'
import type {NetWord} from "@/english/types/vo/netWord/NetWord";
import {useAiChatStore, useWordNetStore} from "@/english/stores";
import {MagicStick} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const wordNetStore = useWordNetStore()
const aiChatStore = useAiChatStore()

// 定义组件属性
const props = defineProps<{
  wordData: NetWord
}>()

const activePos = ref<string>('')

// 按词性分组定义
const groupedDefinitions = computed(() => {
  const groups = new Map<string, any[]>()
  props.wordData.definitions.forEach(def => {
    const key = def.pos || 'other'
    if (!groups.has(key)) {
      groups.set(key, [])
    }
    groups.get(key)!.push(def)
  })
  return groups
})

// 当前激活的词性定义
const activeDefinitions = computed(() => {
  if (!activePos.value && groupedDefinitions.value.size > 0) {
    return Array.from(groupedDefinitions.value.values())[0]
  }
  return groupedDefinitions.value.get(activePos.value) || []
})


//  翻译句子
const translateSentence = async (sentence: string, buttonElement: HTMLElement) => {
  if (aiChatStore.isStreaming) {
    ElMessage.warning('请稍等，正在处理中...' as any)
    return
  }

  let parentElement = buttonElement.parentElement;
  //  获取父元素所有子元素
  const children = Array.from(parentElement?.children || []);
  if (children.length > 1) {
    ElMessage.warning('请勿重复点击' as any)
    return
  }

  const text =  await aiChatStore.chat(sentence, 'TRANSLATE')
  const textElement = document.createElement('p');
  if (text === null) {
    ElMessage.error('翻译失败' as any)
    return
  } else {
    textElement.textContent = text ?? '翻译失败';
  }
  parentElement?.appendChild(textElement)
}

// 初始化激活的词性
onMounted(() => {
  if (groupedDefinitions.value.size > 0) {
    activePos.value = Array.from(groupedDefinitions.value.keys())[0]
  }
})

const getTagType = (pos: string) => {
  switch(pos) {
    case 'noun':
      return 'success';
    case 'verb':
      return 'info';
    case 'adjective':
      return 'warning';
    case 'adverb':
      return 'danger';
    default:
      return 'primary';
  }
}

const handleShowDetails = (word: string) => {
  wordNetStore.fetchWordNet(word)
};
</script>


<template>
  <div class="word-detail-container">
    <!-- 单词标题和基本信息 -->
    <el-card class="word-header-card">
      <template #header>
        <div class="card-header">
          <h1>{{ props.wordData.word }}</h1>
          <el-tag
              v-for="pos in props.wordData.pos"
              :key="pos"
              class="pos-tag"
              :type="getTagType(pos)"
          >
            {{ pos }}
          </el-tag>
        </div>
      </template>

      <div class="basic-info">
        <p v-if="props.wordData.idioms.length > 0">
          <span class="label">相关成语/短语: </span>
          <el-tag
              v-for="idiom in Array.from(props.wordData.idioms)"
              :key="idiom"
              class="info-tag"
              @click="handleShowDetails(idiom)"
          >
            {{ idiom }}
          </el-tag>
        </p>
      </div>
    </el-card>

    <!-- 词性切换按钮 -->
    <div class="pos-tabs" v-if="groupedDefinitions.size > 0">
      <el-button
          v-for="[pos, definitions] in groupedDefinitions"
          :key="pos"
          :type="activePos === pos ? 'primary' : ''"
          :class="{ 'active-tab': activePos === pos }"
          @click="activePos = pos"
          class="pos-tab-button"
      >
        {{ definitions[0]?.posName || pos }}
        <el-tag size="small" :type="getTagType(pos)" class="count-tag">
          {{ definitions.length }}
        </el-tag>
      </el-button>
    </div>

    <!-- 词义定义 -->
    <el-card class="definitions-card" v-if="activeDefinitions.length > 0">
      <template #header>
        <div class="card-header">
          <h2>{{ activeDefinitions[0]?.posName || activePos }} 释义</h2>
        </div>
      </template>

      <div
          v-for="(definition, index) in activeDefinitions"
          :key="index"
          class="definition-item"
      >
        <h3>
          释义 {{ index + 1 }}
        </h3>
        <p class="sub-title">解释:</p>
        <p class="definition-text sentence">
          {{ definition.definition }}
          <el-button
              @click="translateSentence(definition.definition, $event.currentTarget)"
              class="translate-btn">
            <el-icon><MagicStick /></el-icon>
          </el-button>
        </p>

        <div v-if="definition.examples && definition.examples.filter((e: string) => e).length > 0">
          <p class="sub-title">例句:</p>
          <ul>
            <li class="sentence"  v-for="(example, exIndex) in definition.examples.filter((e: string) => e)" :key="exIndex">
              {{ example }}
              <el-button
                  @click="translateSentence(example, $event.currentTarget)"
                  class="translate-btn">
                <el-icon><MagicStick /></el-icon>
              </el-button>
            </li>
          </ul>
        </div>

        <div v-if="definition.synonymExamples && definition.synonymExamples.filter((e: string) => e).length > 0">
          <p class="sub-title">同义词例句:</p>
          <ul>
            <li  class="sentence" v-for="(example, exIndex) in definition.synonymExamples.filter((e: string) => e)" :key="exIndex">
              {{ example }}
              <el-button
                  @click="translateSentence(example, $event.currentTarget)"
                  class="translate-btn">
                <el-icon><MagicStick /></el-icon>
              </el-button>
            </li>
          </ul>
        </div>
      </div>
    </el-card>

    <!-- 关系词汇 -->
    <div class="relation-cards">
      <el-card class="relation-card" v-if="props.wordData.synonyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>同义词</h3>
          </div>
        </template>
        <el-tag
            v-for="synonym in Array.from(props.wordData.synonyms)"
            :key="'synonym-'+synonym"
            type="success"
            class="relation-tag"
            @click="handleShowDetails(synonym)"
        >
          {{ synonym }}
        </el-tag>
      </el-card>

      <el-card class="relation-card" v-if="props.wordData.antonyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>反义词</h3>
          </div>
        </template>
        <el-tag
            v-for="antonym in Array.from(props.wordData.antonyms)"
            :key="'antonym-'+antonym"
            type="danger"
            class="relation-tag"
            @click="handleShowDetails(antonym)"
        >
          {{ antonym }}
        </el-tag>
      </el-card>

      <el-card class="relation-card" v-if="props.wordData.hypernyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>上位词</h3>
          </div>
        </template>
        <el-tag
            v-for="hypernym in Array.from(props.wordData.hypernyms)"
            :key="'hypernym-'+hypernym"
            class="relation-tag"
            @click="handleShowDetails(hypernym)"
        >
          {{ hypernym }}
        </el-tag>
      </el-card>

      <el-card class="relation-card" v-if="props.wordData.hyponyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>下位词</h3>
          </div>
        </template>
        <el-tag
            v-for="hyponym in Array.from(props.wordData.hyponyms)"
            :key="'hyponym-'+hyponym"
            class="relation-tag"
            @click="handleShowDetails(hyponym)"
        >
          {{ hyponym }}
        </el-tag>
      </el-card>

      <el-card class="relation-card" v-if="props.wordData.holonyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>整体词</h3>
          </div>
        </template>
        <el-tag
            v-for="holonym in Array.from(props.wordData.holonyms)"
            :key="'holonym-'+holonym"
            class="relation-tag"
            @click="handleShowDetails(holonym)"
        >
          {{ holonym }}
        </el-tag>
      </el-card>

      <el-card class="relation-card" v-if="props.wordData.meronyms.length > 0">
        <template #header>
          <div class="card-header">
            <h3>部分词</h3>
          </div>
        </template>
        <el-tag
            v-for="meronym in Array.from(props.wordData.meronyms)"
            :key="'meronym-'+meronym"
            class="relation-tag"
            @click="handleShowDetails(meronym)"
        >
          {{ meronym }}
        </el-tag>
      </el-card>
    </div>
  </div>
</template>


<style scoped>
.sentence {
  position: relative;
}

.sentence:hover .translate-btn {
  display: inline;
}

.translate-btn {
  position: absolute;
  display: none;
  background: none;
  border: none;
  height: 25px;
  width: 25px;
}

.word-detail-container {
  max-width: 1000px;
  height: 100%;
  margin: 0 auto;
  overflow-y: auto;
  padding: 20px;
}

.word-detail-container::-webkit-scrollbar {
  display: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h1, .card-header h2, .card-header h3 {
  margin: 0;
}

.pos-tag {
  margin-left: 8px;
}

.basic-info {
  margin-top: 10px;
}

.label {
  font-weight: bold;
  margin-right: 10px;
}

.info-tag {
  margin: 2px 4px;
  cursor: pointer;
}

.info-tag:hover {
  opacity: 0.8;
}

/* 词性切换标签样式 */
.pos-tabs {
  display: flex;
  gap: 8px;
  margin: 20px 0;
  padding: 0 4px;
  overflow-x: auto;
  scrollbar-width: none;
}

.pos-tabs::-webkit-scrollbar {
  display: none;
}

.pos-tab-button {
  position: relative;
  min-width: 100px;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
  white-space: nowrap;
  border: 1px solid #dcdfe6;
  background: #fff;
}

.pos-tab-button:hover {
  border-color: #409EFF;
  color: #409EFF;
}

.pos-tab-button.active-tab {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: white;
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.count-tag {
  position: absolute;
  top: -6px;
  right: -6px;
  min-width: 20px;
  height: 20px;
  line-height: 20px;
  font-size: 12px;
  border-radius: 10px;
}

.definitions-card {
  margin-top: 16px;
  max-height: 100%;
  overflow-y: auto;
}

.definitions-card::-webkit-scrollbar {
  width: 6px;
}

.definitions-card::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.definition-item {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.definition-item:last-child {
  border-bottom: none;
}

.definition-item h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.definition-text {
  font-size: 16px;
  margin: 10px 0;
  line-height: 1.6;
  color: #555;
}

.sub-title {
  font-weight: bold;
  margin: 10px 0 5px 0;
  color: #666;
  font-size: 14px;
}

.definition-item ul {
  margin: 5px 0;
  padding-left: 20px;
}

.definition-item li {
  margin: 4px 0;
  line-height: 1.5;
  color: #555;
}

.relation-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.relation-card {
  height: fit-content;
}

.relation-tag {
  margin: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.relation-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

:deep(.relation-card .el-card__body){
  height: 250px;
  overflow-y: auto;
}


/* 针对Webkit浏览器（Chrome, Safari, Edge） */
:deep(.relation-card .el-card__body)::-webkit-scrollbar {
  width: 6px;
  background: transparent;
}

</style>