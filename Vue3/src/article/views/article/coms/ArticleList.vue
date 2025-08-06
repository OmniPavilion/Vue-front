<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import type { ArticleVO } from '@/article/types/vo/ArticleVO'
import { useArticleStore } from '@/article/stores'
import { useArticleFileStore } from '@/article/stores'
import { ElMessage } from "element-plus"

const articleStore = useArticleStore()
const articleFileStore = useArticleFileStore()
const loading = ref(false)

// 展开/折叠状态 - 独立管理
const expandedYears = ref<Record<string, boolean>>({})
const expandedMonths = ref<Record<string, boolean>>({})

// 从store获取文章数据
const articles = computed(() => articleStore.articles)

// 初始化展开状态（仅在首次加载或文章列表变化时执行）
const initExpandedStates = () => {
  const years = new Set<string>()
  const months = new Set<string>()

  articles.value.forEach(article => {
    const date = new Date(article.writtenAt)
    const year = date.getFullYear().toString()
    const month = `${year}-${date.getMonth() + 1}`
    years.add(year)
    months.add(month)
  })

  // 初始化年份和月份的展开状态（默认收缩）
  years.forEach(year => {
    if (!(year in expandedYears.value)) {
      expandedYears.value[year] = true
    }
  })

  months.forEach(month => {
    if (!(month in expandedMonths.value)) {
      expandedMonths.value[month] = true
    }
  })
}

// 按年份和月份分组文章（不再修改 expandedYears/expandedMonths）
const groupedArticles = computed(() => {
  const groups: Record<string, { total: number; months: Record<string, ArticleVO[]> }> = {}

  articles.value.forEach(article => {
    const date = new Date(article.writtenAt)
    const year = date.getFullYear()
    const month = date.getMonth() + 1

    if (!groups[year]) {
      groups[year] = { total: 0, months: {} }
    }

    if (!groups[year].months[month]) {
      groups[year].months[month] = []
    }

    groups[year].months[month].push(article)
    groups[year].total++
  })

  // 按时间倒序排序
  for (const year in groups) {
    for (const month in groups[year].months) {
      groups[year].months[month].sort((a, b) =>
          new Date(b.writtenAt).getTime() - new Date(a.writtenAt).getTime()
      )
    }
  }

  return groups
})

// 监听 articles 变化，初始化展开状态
watch(articles, () => {
  initExpandedStates()
}, { immediate: true })

// 切换年份展开状态
const toggleYear = (year: string) => {
  expandedYears.value[year] = !expandedYears.value[year]
}

// 切换月份展开状态
const toggleMonth = (year: string, month: string) => {
  const key = `${year}-${month}`
  expandedMonths.value[key] = !expandedMonths.value[key]
}

// 格式化日期显示
const formatDay = (dateString: string) => {
  const date = new Date(dateString)
  return date.getDate() + '日'
}

// 文章点击事件
const handleArticleClick = async (article: ArticleVO) => {
  try {
    if (articleFileStore.isSave === false) {
      ElMessage.error("请先保存当前文章" as any)
      return
    }
    await articleFileStore.fetchArticleFile(article)
  } catch (error) {
    console.error("加载文章失败:", error)
  }
}

// 初始化加载数据
const init = async () => {
  loading.value = true
  try {
    await articleStore.fetchArticlePage()
  } finally {
    loading.value = false
  }
}

init()
</script>

<template>
  <div class="article-timeline">
    <div>
      文章列表
    </div>
    <!-- 加载状态 -->
    <el-skeleton :rows="5" animated v-if="loading" />

    <!-- 时间轴内容 -->
    <div v-else class="timeline-container">
      <!-- 按年份分组 -->
      <div v-for="(yearGroup, year) in groupedArticles" :key="year" class="year-group">
        <div class="year-header" @click="toggleYear(year)">
          <el-icon :class="{ 'rotate-icon': expandedYears[year] }">
            <ArrowRight />
          </el-icon>
          <span class="year-title">{{ year }}年</span>
          <span class="article-count">({{ yearGroup.total }}篇)</span>
        </div>

        <!-- 按月分组 -->
        <el-collapse-transition>
          <div v-show="expandedYears[year]" class="month-groups">
            <div v-for="(monthGroup, month) in yearGroup.months" :key="month" class="month-group">
              <div class="month-header" @click="toggleMonth(year, month)">
                <el-icon :class="{ 'rotate-icon': expandedMonths[`${year}-${month}`] }">
                  <ArrowRight />
                </el-icon>
                <span class="month-title">{{ month }}月</span>
                <span class="article-count">({{ monthGroup.length }}篇)</span>
              </div>

              <!-- 文章列表 -->
              <el-collapse-transition>
                <ul v-show="expandedMonths[`${year}-${month}`]" class="article-list">
                  <li v-for="article in monthGroup" :key="article.id!" class="article-item">
                    <div class="article-date">{{ formatDay(article.writtenAt) }}</div>
                    <div class="article-title" @click="handleArticleClick(article)">
                      {{ article.title }}
                      <el-tag
                          v-if="articleFileStore.currentArticle?.id === article.id"
                          size="small"
                          type="success"
                          style="margin-left: 8px"
                      >
                        当前
                      </el-tag>
                    </div>
                  </li>
                </ul>
              </el-collapse-transition>
            </div>
          </div>
        </el-collapse-transition>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@import "@/article/styles/element/index.scss";

.article-timeline {
  padding: 20px;
  font-family: "Courier New", Courier, monospace;
  height: 90%;
}

.timeline-container {
  max-width: 800px;
  margin: 0 auto;
  height: 100%;
  overflow: auto;
}

.year-group, .month-group {
  margin-bottom: 10px;
}

.year-header, .month-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
  border-radius: 0;
  background-color: transparent;
  border-left: 3px solid transparent;

  &:hover {
    background-color: rgba($notebook-edge, 0.1);
    border-left-color: $primary-color;
  }
}

.year-header {
  font-size: 18px;
  font-weight: bold;
  color: $text-primary;
  margin-top: 20px;
  border-bottom: $border-width $border-style $border-color;
  background-color: rgba($notebook-edge, 0.05);
}

.month-header {
  font-size: 16px;
  color: $text-secondary;
  margin-left: 20px;
  margin-top: 8px;
}

.article-count {
  font-size: 0.8em;
  color: $text-light;
  margin-left: 8px;
}

.article-list {
  list-style: none;
  padding: 0;
  margin-left: 60px;
  border-left: 1px dashed rgba($notebook-edge, 0.3);
}

.article-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  transition: all 0.3s ease;
  position: relative;

  &::before {
    content: "";
    position: absolute;
    left: -20px;
    top: 50%;
    transform: translateY(-50%);
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: $primary-color;
    border: 1px solid darken($primary-color, 10%);
  }

  &:hover {
    background-color: rgba($notebook-edge, 0.08);

    &::before {
      background-color: darken($primary-color, 10%);
    }
  }
}

.article-date {
  width: 40px;
  color: $text-light;
  font-size: 14px;
  font-family: "Times New Roman", serif;
}

.article-title {
  flex: 1;
  cursor: pointer;
  padding: 4px 0;
  color: $text-primary;
  transition: all 0.2s ease;
  position: relative;

  &:hover {
    color: $primary-color;
    text-decoration: none;
  }
}

.rotate-icon {
  transform: rotate(90deg);
}

.el-icon {
  margin-right: 8px;
  transition: transform 0.3s ease;
  color: $notebook-edge;
}

.el-skeleton {
  background-color: rgba($notebook-bg, 0.5) !important;

  :deep(.el-skeleton__item) {
    background-color: rgba($notebook-edge, 0.1) !important;
  }
}

@media (max-width: 768px) {
  .article-list {
    margin-left: 30px;
  }

  .article-item::before {
    left: -15px;
  }
}
</style>