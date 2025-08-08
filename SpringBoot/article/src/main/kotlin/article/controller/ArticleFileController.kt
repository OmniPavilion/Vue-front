package article.controller

import article.constant.ArticleRedisConstant
import article.repository.ArticleRepository.Companion.currentArticleId
import article.service.ArticleService
import common.exception.FileException
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/article/files")
class ArticleFileController(
    private val articleService: ArticleService,
    private val stringRedisTemplate: StringRedisTemplate
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/file/{id}")
    fun getArticleFile(@PathVariable id: Long): Result<String> {
        logger.info { "获取文章文件: $id" }
        currentArticleId = id
        val file = articleService.getArticleFile(id)
        return Result.success(file)
    }

    @PutMapping("/file/{id}")
    fun updateArticleFile(@PathVariable id: Long, @RequestBody file: String): Result<Unit> {
        logger.info { "更新文章文件: $id" }
        articleService.updateArticleFile(id, file)
        return Result.success()
    }

    // 获取根文件夹
    @GetMapping("/rootPath")
    fun getRootPath(): Result<out Any> {
        logger.info { "获取根文件夹" }
        val rootPath = articleService.getRootPath()
        return Result.success(rootPath)
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
        logger.info { "将文件根文件夹重置为默认值" }
        val defaultPath = stringRedisTemplate.opsForHash<String, String>()
            .get(ArticleRedisConstant.FILE_KEY, ArticleRedisConstant.DEFAULT_ROOT_FIELD)
            ?: throw FileException("请先设置默认音乐根目录")
        articleService.updateRoot(defaultPath)
        return Result.success()
    }

    @GetMapping("/downloadAll")
    fun downloadAllArticles(): ResponseEntity<Resource> {
        logger.info { "下载所有文章压缩包" }
        val resource = articleService.downloadAllArticles()

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"articles.zip\"")
            .header("Content-Type", "application/zip")
            .body(resource)
    }
}