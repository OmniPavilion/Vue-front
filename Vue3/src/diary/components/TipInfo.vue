<script setup lang="ts">
import {ElNotification} from "element-plus";
import {onMounted} from "vue";
import {useLogStore, useNoteStore, usePlanStore} from "@/diary/stores";

const logStore = useLogStore();
const noteStore = useNoteStore();
const planStore = usePlanStore();

const initLog = async () => {
  const isTodayHasLog = await logStore.isTodayHasLog();
  if (!isTodayHasLog) {
    ElNotification({
      title: '提示' as any,
      message: '今天还没有写日志哦！' as any,
      type: 'warning' as any,
    })
  }
}

const initNote = async () => {
  const notes = await noteStore.theNoteToDo()
  if (notes.length > 0) {
    ElNotification({
      title: '提示' as any,
      message: '今天有' + notes.length + '个待办事项未完成哦！' as any,
      type: 'warning' as any,
    })
  }
  notes.forEach((note, index) => {
    setTimeout(() => {
      ElNotification({
        title: '提示' as any,
        message: `${note.content} 未完成哦！-- ${note.dueTime}` as any,
        type: 'warning' as any,
      });
    }, index * 1000); // 每隔1秒显示一个通知
  });
}

const initPlan = async () => {
  const plans = await planStore.getPlanGoingOn()
  plans.forEach((plan, index) => {
    setTimeout(() => {
      ElNotification({
        title: '提示' as any,
        message: `${plan.title} 正在进行` as any,
        type: 'warning' as any,
      });
    }, index * 1000); // 每隔1秒显示一个通知
  })
}

onMounted(() => {
  initLog()
  initNote()
  initPlan()
})
</script>

<template>

</template>

<style scoped>

</style>
