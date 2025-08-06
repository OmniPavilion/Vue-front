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
    const month = `${year}-${(date.getMonth() + 1).toString().padStart(2, '0')}` // 补零保持一致
    years.add(year)
    months.add(month)
  })

  // 默认展开所有年份和月份
  years.forEach(year => {
    expandedYears.value[year] = true
  })

  months.forEach(month => {
    expandedMonths.value[month] = true
  })

  console.log('展开状态初始化完成', {
    years: [...years],
    months: [...months],
    expandedYears: expandedYears.value,
    expandedMonths: expandedMonths.value
  })
}
// 按年份和月份分组文章（不再修改 expandedYears/expandedMonths）
const groupedArticles = computed(() => {
  // 1. 首先按时间正序排序所有文章（确保日正序）
  const sortedArticles = [...articles.value].sort((a, b) =>
      new Date(a.writtenAt).getTime() - new Date(b.writtenAt).getTime()
  );

  // 2. 创建分组结构
  const result: {
    year: string;
    total: number;
    months: {
      month: string;
      articles: ArticleVO[];
    }[];
  }[] = [];

  // 3. 临时存储年份索引
  const yearIndexMap: Record<string, number> = {};
  const monthIndexMap: Record<string, Record<string, number>> = {};

  // 4. 进行分组
  sortedArticles.forEach(article => {
    const date = new Date(article.writtenAt);
    const year = date.getFullYear().toString();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 补零

    // 处理年份
    if (yearIndexMap[year] === undefined) {
      yearIndexMap[year] = result.length;
      result.push({
        year,
        total: 0,
        months: []
      });

      // 初始化月份的索引映射
      monthIndexMap[year] = {};
    }

    // 处理月份
    if (monthIndexMap[year][month] === undefined) {
      monthIndexMap[year][month] = result[yearIndexMap[year]].months.length;
      result[yearIndexMap[year]].months.push({
        month,
        articles: []
      });
    }

    // 添加文章（日正序）
    result[yearIndexMap[year]].months[monthIndexMap[year][month]].articles.push(article);
    result[yearIndexMap[year]].total++;
  });

  // 5. 年倒序、月正序排序
  result.sort((a, b) => Number(b.year) - Number(a.year)); // 年倒序

  result.forEach(yearGroup => {
    yearGroup.months.sort((a, b) => Number(a.month) - Number(b.month)); // 月正序
  });

  return result;
});

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
      <div v-for="yearGroup in groupedArticles" :key="yearGroup.year" class="year-group">
        <div class="year-header" @click="toggleYear(yearGroup.year)">
          <el-icon :class="{ 'rotate-icon': expandedYears[yearGroup.year] }">
            <ArrowRight />
          </el-icon>
          <span class="year-title">{{ yearGroup.year }}年</span>
          <span class="article-count">({{ yearGroup.total }}篇)</span>
        </div>

        <!-- 按月分组 -->
        <el-collapse-transition>
          <div v-show="expandedYears[yearGroup.year]" class="month-groups">
            <div v-for="monthGroup in yearGroup.months" :key="monthGroup.month" class="month-group">
              <div class="month-header" @click="toggleMonth(yearGroup.year, monthGroup.month)">
                <el-icon :class="{ 'rotate-icon': expandedMonths[`${yearGroup.year}-${monthGroup.month}`] }">
                  <ArrowRight />
                </el-icon>
                <span class="month-title">{{ monthGroup.month }}月</span>
                <span class="article-count">({{ monthGroup.articles.length }}篇)</span>
              </div>

              <!-- 文章列表 -->
              <el-collapse-transition>
                <ul v-show="expandedMonths[`${yearGroup.year}-${monthGroup.month}`]" class="article-list">
                  <li v-for="article in monthGroup.articles" :key="article.id!" class="article-item">
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
      </div>    </div>
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