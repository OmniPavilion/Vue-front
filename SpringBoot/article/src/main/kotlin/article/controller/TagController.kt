package article.controller

import article.pojo.po.Tag
import article.service.TagService
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article/categories")
class TagController(
    private val tagService: TagService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createTag(@RequestBody tag: article.pojo.vo.TagVO): Result<Long> {
        logger.info { "创建分类: $tag" }
        val id = tagService.createTag(tag)
        return Result.success(id)
    }

    @GetMapping("/{id}")
    fun getTag(@PathVariable id: Long): Result<Tag> {
        logger.info { "获取分类: $id" }
        val tag = tagService.getTagById(id)
        return Result.success(tag)
    }

    @PutMapping
    fun updateTag(@RequestBody tag: article.pojo.vo.TagVO): Result<Unit> {
        logger.info { "更新分类: $tag" }
        tagService.updateTag(tag)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除分类: $id" }
        tagService.deleteTag(id)
        return Result.success()
    }

    @GetMapping
    fun getAllTags(): Result<List<Tag>> {
        logger.info { "获取全部分类" }
        val categories = tagService.getAllTags()
        return Result.success(categories)
    }

    @PostMapping("/page")
    fun getTagPage(@RequestBody pageDTO: PageDTO<String>): Result<PageVO<Tag>> {
        logger.info { "分页查询分类: $pageDTO" }
        val page = tagService.getTagPage(pageDTO)
        return Result.success(page)
    }
}