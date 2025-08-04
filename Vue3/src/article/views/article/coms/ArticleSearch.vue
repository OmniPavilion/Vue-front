<script setup lang="ts">
import {ref, watch} from 'vue'
import { useArticleStore } from '@/article/stores'
import { useTagStore } from '@/article/stores'
import EditTagButton from "@/article/views/article/coms/EditTagButton.vue";

const articleStore = useArticleStore()
const tagStore = useTagStore()

// 初始化查询参数
const queryParams = ref({
  title: '',
  tagId: null
})

// 查询方法
const handleQuery = async () => {
  // 更新store中的查询条件
  articleStore.pageQuery.query = {
    title: queryParams.value.title,
    tagId: queryParams.value.tagId
  }

  // 重置页码为第一页
  articleStore.pageQuery.pageNum = 1

  // 执行查询
  await articleStore.fetchArticlePage()
}

// 初始化加载标签数据
const init = async () => {
  await tagStore.fetchAllTags()
}

watch([() => queryParams.value.tagId, () => queryParams.value.title], async () => {
  await handleQuery()
})

init()
</script>

<template>
  <el-card class="search-container">
    <el-form :model="queryParams" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="文章名称">
            <el-input
                v-model="queryParams.title"
                placeholder="请输入文章名称"
                clearable
            />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="文章分类">
            <div style="display: flex; gap: 10px; align-items: center; width: 100%">
              <el-select
                  v-model="queryParams.tagId"
                  placeholder="请选择分类"
                  clearable
              >
                <el-option
                    v-for="tag in tagStore.allTags"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                />
              </el-select>
              <EditTagButton></EditTagButton>
            </div>
          </el-form-item>

        </el-col>

      </el-row>
    </el-form>
  </el-card>
</template>

<style scoped lang="scss">
@import "@/article/styles/element/index.scss"; // 导入主题变量

.search-container {
  margin-bottom: 20px;
  background-color: rgba($notebook-bg, 0.9);
  border: $border-width $border-style $border-color;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.button-group {
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .el-button {
    margin-left: 10px;
  }
}

.el-form-item {
  margin-bottom: 0;
}
</style>