<script setup>
import Background from '@/diary/components/Background.vue';
import Bookmark from '@/diary/components/Bookmark.vue';
import TipInfo from "@/diary/components/TipInfo.vue";
</script>

<template>
  <TipInfo></TipInfo>
  <Background
      theme="vintage"
      :showDecorativeEdge="true"
      :showGrid="true"
  />
  <Bookmark></Bookmark>
  <RouterView></RouterView>
</template>

<style scoped>
* {
  user-select: none;
}
</style>
