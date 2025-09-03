<script setup lang="ts">

</script>

<template>
<div class="poster-box">

</div>
</template>

<style scoped>
.poster-box {
  width: 150px;
  height: 72px;
  background-image: url('@/english/public/title.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  margin-left: auto;
  transform: translateX(calc((960px - 120vw) * 0.7));
  transition: transform 0.7s ease;
  z-index: 0;
}
</style>
