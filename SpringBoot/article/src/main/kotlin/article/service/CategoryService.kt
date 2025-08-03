package article.service

import article.exception.CategoryException
import article.pojo.po.Category
import article.pojo.vo.CategoryVO
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO

interface CategoryService {
    @Throws(CategoryException::class)
    fun createCategory(category: CategoryVO): Long

    @Throws(CategoryException::class)
    fun getCategoryById(id: Long): Category

    @Throws(CategoryException::class)
    fun updateCategory(category: CategoryVO)

    @Throws(CategoryException::class)
    fun deleteCategory(id: Long)

    @Throws(CategoryException::class)
    fun getAllCategories(): List<Category>

    @Throws(CategoryException::class)
    fun getCategoryPage(pageDTO: PageDTO<String>): PageVO<Category>
}