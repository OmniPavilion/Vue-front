<script setup lang="ts">
import ListCard from "@/diary/views/log/com/ListCard.vue";
import Search from "@/diary/views/log/com/Search.vue";
</script>

<template>
  <div class="log-container">
    <Search></Search>
    <ListCard></ListCard>
  </div>
</template>

<style scoped>
.log-container {
  height: 100%;
}

</style>
