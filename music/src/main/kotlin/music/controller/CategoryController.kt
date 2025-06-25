package music.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.pojo.vo.CategoryVO
import music.service.CategoryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {
    private  val logger = KotlinLogging.logger {  }

    /**
     * 获取分类列表(分页)
     */
    @GetMapping("/page")
    fun getCategories(@RequestBody pageDTO: PageDTO<String>): Result<PageVO<CategoryVO>> {
        logger.info { "获取分类列表(分页)：${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val categoryPage = categoryService.getCategoryPage(pageDTO)
        return Result.success(categoryPage)
    }

    /**
     * 获取单个分类详情
     */
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): Result<CategoryVO> {
        logger.info { "获取单个分类详情：$id" }
        val categoryById = categoryService.getCategoryById(id)
        return Result.success(categoryById)
    }

    /**
     * 创建分类
     */
    @PostMapping
    fun createCategory(@RequestBody categoryVO: CategoryVO): Result<Unit> {
        logger.info { "创建分类：${JSON.toJSONString(categoryVO, SerializerFeature.PrettyFormat)}" }
        categoryService.createCategory(categoryVO)
        return Result.success()
    }

    /**
     * 更新分类
     */
    @PutMapping
    fun updateCategory(@RequestBody categoryVO: CategoryVO): Result<Unit> {
        logger.info { "更新分类：${JSON.toJSONString(categoryVO, SerializerFeature.PrettyFormat)}" }
        categoryService.updateCategory(categoryVO)
        return Result.success()
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): Result<Unit> {
        categoryService.deleteCategory(id)
        return Result.success()
    }
}