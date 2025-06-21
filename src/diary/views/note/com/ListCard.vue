<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useNoteStore } from "@/diary/stores";
import NoteCard from "@/diary/views/note/com/NoteCard.vue";

const noteStore = useNoteStore();
const containerRef = ref<HTMLElement | null>(null);

onMounted(() => {
  noteStore.fetchNotePage();
});
</script>

<template>
  <div class="list-outer-container">
    <div class="list-container" ref="containerRef">
      <div class="cards-wrapper">
        <NoteCard
            v-for="(note, index) in noteStore.notes"
            :key="index"
            :note="note"
        ></NoteCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.list-outer-container {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 0 20px;
}

.list-container {
  width: 90%;
  max-width: 800px;
  height: calc(100vh - 120px);
  overflow-x: hidden;
  overflow-y: scroll; /* 改为scroll确保始终有滚动条空间 */
  padding: 8px 12px;
  scrollbar-width: none; /* Firefox隐藏滚动条 */
  -ms-overflow-style: none; /* IE和Edge隐藏滚动条 */
}

/* Chrome/Safari/Opera隐藏滚动条 */
.list-container::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
  background: transparent;
}

.cards-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-bottom: 20px;
}

.note-card-item {
  flex-shrink: 0;
  transition: transform 0.3s ease, opacity 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .list-outer-container {
    padding: 0 12px;
  }

  .list-container {
    height: calc(100vh - 160px);
    padding: 8px;
    max-width: 100%;
  }

  .cards-wrapper {
    gap: 10px;
  }
}

@media (min-width: 1200px) {
  .list-container {
    max-width: 900px;
  }
}
</style>
