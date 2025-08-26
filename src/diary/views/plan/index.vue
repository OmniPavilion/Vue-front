<script setup lang="ts">

import ListCard from "@/diary/views/plan/com/ListCard.vue";
import Search from "@/diary/views/plan/com/Search.vue";
</script>

<template>
  <div class="plan-container">
    <Search></Search>
    <ListCard></ListCard>
  </div>
</template>

<style scoped>
.plan-container {
  height: 100%;
}

</style>
