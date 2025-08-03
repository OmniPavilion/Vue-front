package article.controller

import article.constant.ArticleRedisConstant
import article.pojo.vo.ArticleVO
import article.service.ArticleService
import common.exception.FileException
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/articles")
class ArticleController(
    private val articleService: ArticleService,
    private val stringRedisTemplate: StringRedisTemplate
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createArticle(@RequestBody article: ArticleVO): Result<Long> {
        logger.info { "创建空文章: $article" }
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
        logger.info { "更新文章: $article" }
        articleService.updateArticle(article)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除文章: $id" }
        articleService.deleteArticle(id)
        return Result.success()
    }

    @PostMapping("/page")
    fun getArticlePage(@RequestBody pageDTO: PageDTO<Unit>): Result<PageVO<ArticleVO>> {
        logger.info { "分页查询文章: $pageDTO" }
        val page = articleService.getArticlePage(pageDTO)
        return Result.success(page)
    }

    @GetMapping("/file/{id}")
    fun getArticleFile(@PathVariable id: Long): Result<String> {
        logger.info { "获取文章文件: $id" }
        val file = articleService.getArticleFile(id)
        return Result.success(file)
    }

    @PutMapping("/file/{id}")
    fun updateArticleFile(@PathVariable id: Long, @RequestParam("file") file: String): Result<Unit> {
        logger.info { "更新文章文件: $id" }
        articleService.updateArticleFile(id, file)
        return Result.success()
    }

    // 修改根文件夹
    @PutMapping("/root")
    fun updateRoot(@RequestParam path: String) : Result<Unit> {
        logger.info { "将音乐文件根文件夹修改为： $path" }
        articleService.updateRoot(path)
        return Result.success()
    }

    // 重置音乐文件根文件夹
    @PutMapping("/resetRoot")
    fun resetRoot() : Result<Unit> {
        logger.info { "将音乐文件根文件夹重置为默认值" }
        val defaultPath = stringRedisTemplate.opsForHash<String, String>()
            .get(ArticleRedisConstant.FILE_KEY, ArticleRedisConstant.DEFAULT_ROOT_FIELD)
            ?: throw FileException("请先设置默认音乐根目录")
        articleService.updateRoot(defaultPath)
        return Result.success()
    }
}