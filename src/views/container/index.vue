<script setup lang="ts">
// 无需额外脚本
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-header>Header</el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
/* 关键样式修改 */
.common-layout {
  height: 100%;
  width: 100%;
  padding: 0;
  margin: 1px;
  border: #1a1a1a 5px solid;
  background-color: pink;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #b3c0d1;
  display: flex;
  align-items: center;
}

.el-aside {
  background-color: #d3dce6;
}

.el-main {
  background-color: #e9eef3;
  padding: 0; /* 移除默认内边距 */
}

/* 可选：添加过渡效果 */
.el-header, .el-aside, .el-main {
  transition: all 0.3s;
}
</style>
