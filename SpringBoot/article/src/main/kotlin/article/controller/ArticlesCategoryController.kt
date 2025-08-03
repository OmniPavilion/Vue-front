package article.controller

import article.pojo.po.Category
import article.pojo.vo.CategoryVO
import article.service.CategoryService
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article/categories")
class ArticlesCategoryController(
    private val categoryService: CategoryService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createCategory(@RequestBody category: CategoryVO): Result<Long> {
        logger.info { "创建分类: $category" }
        val id = categoryService.createCategory(category)
        return Result.success(id)
    }

    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: Long): Result<Category> {
        logger.info { "获取分类: $id" }
        val category = categoryService.getCategoryById(id)
        return Result.success(category)
    }

    @PutMapping
    fun updateCategory(@RequestBody category: CategoryVO): Result<Unit> {
        logger.info { "更新分类: $category" }
        categoryService.updateCategory(category)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除分类: $id" }
        categoryService.deleteCategory(id)
        return Result.success()
    }

    @GetMapping
    fun getAllCategories(): Result<List<Category>> {
        logger.info { "获取全部分类" }
        val categories = categoryService.getAllCategories()
        return Result.success(categories)
    }

    @PostMapping("/page")
    fun getCategoryPage(@RequestBody pageDTO: PageDTO<String>): Result<PageVO<Category>> {
        logger.info { "分页查询分类: $pageDTO" }
        val page = categoryService.getCategoryPage(pageDTO)
        return Result.success(page)
    }
}