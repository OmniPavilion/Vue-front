<script setup lang="ts">
import FileList from "@/bibliophile/components/FileList.vue";
import Background from "@/bibliophile/components/Background.vue";

import { useAiReadStore } from "@/bibliophile/stores";
import AiReader from "@/bibliophile/components/AiReader.vue";

const aiReadStore = useAiReadStore();
</script>

<template>
  <Background>
    <div class="container">
      <FileList v-show="!aiReadStore.currentFile"></FileList>
      <AiReader v-if="aiReadStore.currentFile"></AiReader>
    </div>
  </Background>
</template>

<style scoped>
.container {
  height: 100vh;
}
</style>
