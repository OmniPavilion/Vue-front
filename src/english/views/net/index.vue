<script setup lang="ts">
import WordTable from "@/english/views/net/com/WordTable.vue";
import WordDetail from "@/english/views/net/com/WordDetail.vue";

import {useWordNetStore} from "@/english/stores";
import WordTag from "@/english/views/net/com/WordTag.vue";

const wordNetStore = useWordNetStore();

</script>

<template>
  <div class="word-net-page">
    <div class="word-tag-container">
      <WordTag></WordTag>
    </div>
    <div class="word-net-container">
      <WordTable v-show="!wordNetStore.currentShowNetWord"></WordTable>
      <WordDetail v-if="wordNetStore.currentShowNetWord"  :word-data="wordNetStore.currentShowNetWord"></WordDetail>
    </div>
  </div>


</template>

<style scoped>
.word-net-page {
  height: 100%;
}


.word-net-container {
  height:87%;
}

.word-tag-container {
  height: 13%;
}
</style>