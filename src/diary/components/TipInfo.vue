<script setup lang="ts">
import {ElNotification} from "element-plus";
import {onMounted} from "vue";
import {useLogStore} from "@/diary/stores";

const logStore = useLogStore();

const init = async () => {
  const isTodayHasLog = await logStore.isTodayHasLog();
  if (!isTodayHasLog) {
    ElNotification({
      title: '提示' as any,
      message: '今天还没有写日志哦！' as any,
      type: 'warning' as any,
    })
  }
}

onMounted(() => {
  init()
})
</script>

<template>

</template>

<style scoped>

</style>
