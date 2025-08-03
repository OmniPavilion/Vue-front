package article.service.impl

import article.exception.CategoryException
import article.mapper.ArticlesCategoryMapper
import article.pojo.po.Category
import article.pojo.vo.CategoryVO
import article.service.CategoryService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.ARTICLE)
class ArticlesCategoryServiceImpl(
    private val categoryMapper: ArticlesCategoryMapper
) : CategoryService {
    override fun createCategory(category: CategoryVO): Long {
        val now = LocalDateTime.now()
        val po = Category(
            name = category.name,
            createdAt = now,
            updatedAt = now
        )

        if (po.name.isBlank()) {
            throw CategoryException("分类名称不能为空")
        }

        if (categoryMapper.selectCount(
                KtQueryWrapper(Category::class.java)
                    .eq(Category::name, po.name)
            ) > 0
        ) {
            throw CategoryException("分类名称已存在")
        }

        if (categoryMapper.insert(po) != 1) {
            throw CategoryException("创建分类失败")
        }

        return po.id ?: throw CategoryException("获取分类ID失败")
    }

    override fun getCategoryById(id: Long): Category {
        return categoryMapper.selectById(id)
            ?: throw CategoryException("未找到ID为${id}的分类")
    }

    override fun updateCategory(category: CategoryVO) {
        if (category.id == null) {
            throw CategoryException("未指定分类ID")
        }

        val existing = getCategoryById(category.id)
        val updated = existing.copy(
            name = category.name,
            updatedAt = LocalDateTime.now()
        )

        if (updated.name.isBlank()) {
            throw CategoryException("分类名称不能为空")
        }

        if (existing.name != updated.name) {
            if (categoryMapper.selectCount(
                    KtQueryWrapper(Category::class.java)
                        .eq(Category::name, updated.name)
                ) > 0
            ) {
                throw CategoryException("分类名称已存在")
            }
        }

        if (categoryMapper.updateById(updated) != 1) {
            throw CategoryException("更新分类失败")
        }
    }

    override fun deleteCategory(id: Long) {
        // 检查是否有关联文章
        val count = categoryMapper.selectCount(
            KtQueryWrapper(Category::class.java)
                .eq(Category::id, id)
        )

        if (count == 0L) {
            throw CategoryException("未找到ID为${id}的分类")
        }

        if (categoryMapper.deleteById(id) != 1) {
            throw CategoryException("删除分类失败")
        }
    }

    override fun getAllCategories(): List<Category> {
        return categoryMapper.selectList(null)
    }

    override fun getCategoryPage(pageDTO: PageDTO<String>): PageVO<Category> {
        val page = Page<Category>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val queryWrapper = KtQueryWrapper(Category::class.java).apply {
            if (pageDTO.query != null) {
                like(Category::name, pageDTO.query)
            }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Category::id)
                SortDirection.DESC -> orderByDesc(Category::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = categoryMapper.selectPage(page, queryWrapper)
        return PageVO(result.total, result.records)
    }

}