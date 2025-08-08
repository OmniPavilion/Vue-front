<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useTagStore } from '@/article/stores';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Expand } from "@element-plus/icons-vue";
import type { TagVO } from "@/article/types/vo/TagVO";

const tagStore = useTagStore();

// 对话框状态
const dialogVisible = ref(false);
const editDialogVisible = ref(false);

// 标签数据
const tags = ref<TagVO[]>([]);
const newTag = ref({
  name: ''
});
const currentTag = ref<TagVO>({
  id: 0,
  name: ''
});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 打开标签对话框
const openTagDialog = async () => {
  dialogVisible.value = true;
  await loadTags();
};

// 加载标签
const loadTags = async () => {
  try {
    tagStore.pageQuery.pageNum = currentPage.value;
    tagStore.pageQuery.pageSize = pageSize.value;

    await tagStore.fetchTagPage();
    tags.value = tagStore.tags;
    total.value = tagStore.total;
  } catch (error) {
    ElMessage.error('加载标签失败' as any);
    console.error(error);
  }
};

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadTags();
};

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
  loadTags();
};

// 添加标签
const handleAdd = async () => {
  if (!newTag.value.name) {
    ElMessage.warning('请输入标签名称' as any);
    return;
  }

  try {
    const res = await tagStore.createTag({
      name: newTag.value.name
    } as TagVO);

    if (res.code === -1) {
      ElMessage.error(res.message as any);
      return;
    }

    ElMessage.success('添加成功' as any);
    newTag.value.name = '';
    // 添加后重新加载第一页
    currentPage.value = 1;
    await loadTags();
  } catch (error) {
    ElMessage.error('添加标签失败' as any);
    console.error(error);
  }
};

// 编辑标签
const handleEdit = (tag: TagVO) => {
  currentTag.value = { ...tag };
  editDialogVisible.value = true;
};

const confirmEdit = async () => {
  try {
    await tagStore.updateTag(currentTag.value);
    ElMessage.success('更新成功' as any);
    editDialogVisible.value = false;
    await loadTags();
  } catch (error) {
    ElMessage.error('更新标签失败' as any);
    console.error(error);
  }
};

// 删除标签
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个标签吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await tagStore.deleteTag(id);
    ElMessage.success('删除成功' as any);

    // 如果当前页最后一条被删除，且不是第一页，则返回上一页
    if (tags.value.length === 1 && currentPage.value > 1) {
      currentPage.value -= 1;
    }

    await loadTags();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除标签失败' as any);
      console.error(error);
    }
  }
};

// 初始化加载标签
onMounted(async () => {
  await loadTags();
});
</script>

<template>

  <div>
    <!-- 标签管理按钮 -->
    <el-button  circle @click="openTagDialog">
      <el-icon><Expand /></el-icon>
    </el-button>

    <!-- 标签管理对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="标签管理"
        width="800px"
        :close-on-click-modal="false"
        append-to-body
    >
      <div class="dialog-content-wrapper">
        <!-- 标签列表 -->
        <div class="table-wrapper">
          <el-table
              :data="tags"
              border
              style="width: 100%"
              height="400"
              v-loading="tagStore.loading"
          >
            <el-table-column prop="id" label="ID" width="80">
              <template #default="{ $index }">
                {{ (currentPage - 1) * pageSize + $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="name" label="标签名称" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button
                    size="small"
                    @click="handleEdit(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                    size="small"
                    type="danger"
                    @click="handleDelete(scope.row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页组件 -->
          <div class="pagination-wrapper">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @current-change="handlePageChange"
                @size-change="handleSizeChange"
            />
          </div>
        </div>

        <!-- 添加标签表单 -->
        <el-divider />
        <div class="form-wrapper">
          <el-form
              @submit.native.prevent
              :model="newTag"
              label-width="80px"
          >
            <el-form-item label="标签名称">
              <el-input v-model="newTag.name" />
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  @click="handleAdd"
              >
                添加标签
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑标签对话框 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑标签"
        width="400px"
        :close-on-click-modal="false"
        append-to-body
    >
      <div class="edit-dialog-content">
        <el-form :model="currentTag" label-width="80px">
          <el-form-item label="标签名称">
            <el-input v-model="currentTag.name" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button
            type="primary"
            @click="confirmEdit"
            :loading="tagStore.loading"
        >
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 对话框内容容器 */
.dialog-content-wrapper {
  max-height: 70vh;
}

/* 表格容器 */
.table-wrapper {
  margin-bottom: 20px;
}

/* 分页容器 */
.pagination-wrapper {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}

/* 表单容器 */
.form-wrapper {
  margin-top: 20px;
}

/* 编辑对话框内容 */
.edit-dialog-content {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}

/* 表格固定高度 */
.el-table {
  --el-table-border-color: rgba(120, 230, 255, 0.1);
  --el-table-border: 1px solid var(--el-table-border-color);
}

/* 适配移动端 */
@media (max-width: 768px) {
  .dialog-content-wrapper {
    max-height: 60vh;
  }

  .el-table {
    height: 300px;
  }

  .pagination-wrapper {
    flex-wrap: wrap;
  }
}
</style>