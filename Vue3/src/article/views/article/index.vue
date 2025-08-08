<script setup lang="ts">

import ArticleList from "@/article/views/article/coms/ArticleList.vue";
import ArticleSearch from "@/article/views/article/coms/ArticleSearch.vue";
</script>

<template>
  <div class="article-list-page">
    <ArticleSearch></ArticleSearch>
    <ArticleList></ArticleList>
  </div>
</template>

<style scoped>
.article-list-page {
  height: 100%;
}

</style>