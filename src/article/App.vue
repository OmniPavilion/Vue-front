<script setup lang="ts">
import Background from "@/article/components/Background.vue";
import ExpandableLayout from "@/article/components/ExpandableLayout.vue";
import Read from '@/article/views/read/index.vue'
import Ai from '@/article/views/ai/index.vue'
import Article from '@/article/views/article/index.vue'
import {onMounted} from "vue";

import {useArticleStore} from "@/article/stores";
import {useArticleFileStore} from "@/article/stores";

const articleStore = useArticleStore()
const articleFileStore = useArticleFileStore()

onMounted(async () => {
  const res01 = await articleStore.getCurrentArticle()
  if (res01.code !== 1) {
     return
  }

  const res02 =  await articleStore.fetchArticle(res01.data)
  if (res02.code !== 1) {
     return
  }

  await articleFileStore.fetchArticleFile(res02.data)

})
</script>

<template>
  <Background></Background>
  <ExpandableLayout>
    <template #left>
      <Article></Article>
    </template>

    <Read></Read>

    <template #right>
     <Ai></Ai>
    </template>
  </ExpandableLayout>
</template>

<style scoped>

</style>
