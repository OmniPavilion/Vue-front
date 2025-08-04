<script setup lang="ts">
import Background from "@/article/components/Background.vue";
import ExpandableLayout from "@/article/components/ExpandableLayout.vue";
import Read from '@/article/views/read/index.vue'
import Ai from '@/article/views/ai/index.vue'
import Article from '@/article/views/article/index.vue'
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
