<script setup lang="ts">
import SingerSideList from "@/music/views/singerSide/com/SingerSideList.vue";
import EditCategoryButton from "@/music/views/singerSide/com/EditCategoryButton.vue";
import SettingButton from "@/music/views/singerSide/com/SettingButton.vue";
</script>

<template>
  <div class="singer-container">
    <div class="singer-aside">
      <div class="button-group">
        <SettingButton class="aside-button" />
        <EditCategoryButton class="aside-button" />
      </div>
    </div>
    <SingerSideList />
  </div>
</template>

<style scoped>
.singer-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.singer-aside {
  padding: 12px 0;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.button-group {
  display: flex;
  gap: 16px; /* 按钮间距 */
  align-items: center;
}

.aside-button {
  transition: all 0.3s ease;
}

.aside-button:hover {
  transform: translateY(-2px);
  filter: brightness(1.1);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .button-group {
    gap: 12px;
  }

  .singer-aside {
    padding: 8px 0;
  }
}
</style>
