<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useCategoryStore } from '@/music/stores';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { CategoryVO } from "@/music/types/vo/CategoryVO";

const categoryStore = useCategoryStore();

// 对话框状态
const dialogVisible = ref(false);
const editDialogVisible = ref(false);

// 分类数据
const categories = ref<CategoryVO[]>([]);
const newCategory = ref({
  name: ''
});
const currentCategory = ref<CategoryVO>({
  id: 0,
  name: ''
});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 打开分类对话框
const openCategoryDialog = async () => {
  dialogVisible.value = true;
  await loadCategories();
};

// 加载分类
const loadCategories = async () => {
  try {
    categoryStore.pageQuery.pageNum = currentPage.value;
    categoryStore.pageQuery.pageSize = pageSize.value;

    await categoryStore.fetchCategoryPage();
    categories.value = categoryStore.categories;
    total.value = categoryStore.total;
  } catch (error) {
    ElMessage.error('加载分类失败' as any);
    console.error(error);
  }
};

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadCategories();
};

// 处理每页数量变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
  loadCategories();
};

// 添加分类
const handleAdd = async () => {
  if (!newCategory.value.name) {
    ElMessage.warning('请输入分类名称' as any);
    return;
  }

  try {
    const res = await categoryStore.createCategory({
      name: newCategory.value.name
    } as CategoryVO);

    if (res.code === -1) {
      ElMessage.error(res.message as any);
      return;
    }

    ElMessage.success('添加成功' as any);
    newCategory.value.name = '';
    // 添加后重新加载第一页
    currentPage.value = 1;
    await loadCategories();
  } catch (error) {
    ElMessage.error('添加分类失败' as any);
    console.error(error);
  }
};

// 编辑分类
const handleEdit = (category: CategoryVO) => {
  currentCategory.value = { ...category };
  editDialogVisible.value = true;
};

const confirmEdit = async () => {
  try {
    await categoryStore.updateCategory(currentCategory.value);
    ElMessage.success('更新成功' as any);
    editDialogVisible.value = false;
    await loadCategories();
  } catch (error) {
    ElMessage.error('更新分类失败' as any);
    console.error(error);
  }
};

// 删除分类
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await categoryStore.deleteCategory(id);
    ElMessage.success('删除成功' as any);

    // 如果当前页最后一条被删除，且不是第一页，则返回上一页
    if (categories.value.length === 1 && currentPage.value > 1) {
      currentPage.value -= 1;
    }

    await loadCategories();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除分类失败' as any);
      console.error(error);
    }
  }
};

// 初始化加载分类
onMounted(async () => {
  await loadCategories();
});
</script>

<template>
  <div>
    <!-- 分类管理按钮 -->
    <el-button type="primary" @click="openCategoryDialog">
      管理分类
    </el-button>

    <!-- 分类管理对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="分类管理"
        width="800px"
        :close-on-click-modal="false"
    >
      <div class="dialog-content-wrapper">
        <!-- 分类列表 -->
        <div class="table-wrapper">
          <el-table
              :data="categories"
              border
              style="width: 100%"
              height="400"
              v-loading="categoryStore.loading"
          >
            <el-table-column prop="id" label="ID" width="80">
              <template #default="{ $index }">
                {{ (currentPage - 1) * pageSize + $index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="name" label="分类名称" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
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

        <!-- 添加分类表单 -->
        <el-divider />
        <div class="form-wrapper">
          <el-form
              :model="newCategory"
              label-width="80px"
          >
            <el-form-item label="分类名称">
              <el-input v-model="newCategory.name" />
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  @click="handleAdd"
                  :loading="categoryStore.loading"
              >
                添加分类
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑分类对话框 -->
    <el-dialog
        v-model="editDialogVisible"
        title="编辑分类"
        width="400px"
        :close-on-click-modal="false"
    >
      <div class="edit-dialog-content">
        <el-form :model="currentCategory" label-width="80px">
          <el-form-item label="分类名称">
            <el-input v-model="currentCategory.name" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button
            type="primary"
            @click="confirmEdit"
            :loading="categoryStore.loading"
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
