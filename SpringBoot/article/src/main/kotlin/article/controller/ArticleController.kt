package article.controller

import article.pojo.dto.ArticleQuery
import article.pojo.vo.ArticleVO
import article.repository.ArticleRepository.Companion.currentArticleId
import article.service.ArticleAiMemoryService
import article.service.ArticleService
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article/articles")
class ArticleController(
    private val articleService: ArticleService,
    private val articleAiService: ArticleAiMemoryService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createArticle(@RequestBody article: ArticleVO): Result<Long> {
        logger.info { "创建空文章: ${JSON.toJSONString( article, SerializerFeature.PrettyFormat)}" }
        val id = articleService.createArticle(article)
        return Result.success(id)
    }

    @GetMapping("/{id}")
    fun getArticle(@PathVariable id: Long): Result<ArticleVO> {
        logger.info { "获取文章: $id" }
        val article = articleService.getArticleById(id)
        return Result.success(article)
    }

    @PutMapping
    fun updateArticle(@RequestBody article: ArticleVO): Result<Unit> {
        logger.info { "更新文章: ${JSON.toJSONString( article, SerializerFeature.PrettyFormat)}" }
        articleService.updateArticle(article)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除文章: $id" }
        articleService.deleteArticle(id)
        articleAiService.removeByArticleId(id)
        return Result.success()
    }

    @PostMapping("/page")
    fun getArticlePage(@RequestBody pageDTO: PageDTO<ArticleQuery>): Result<PageVO<ArticleVO>> {
        logger.info { "分页查询文章: ${JSON.toJSONString( pageDTO, SerializerFeature.PrettyFormat)}" }
        val page = articleService.getArticlePage(pageDTO)
        return Result.success(page)
    }

    @GetMapping("/current/id")
    fun getCurrentArticle(): Result<Long> {
        return Result.success(currentArticleId)
    }
}