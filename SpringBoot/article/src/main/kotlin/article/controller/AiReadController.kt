package article.controller

import article.advise.ArticleAdvisor
import article.service.ArticleAiMemoryService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.pojo.po.SpringAiChatMemory
import mu.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import common.pojo.vo.Result
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDate

@RestController
@RequestMapping("/article/read")
class AiReadController(
    private val articleChatClient: ChatClient,
    private val articleAiMemoryService: ArticleAiMemoryService
) {
    private val logger = KotlinLogging.logger { }

    // 读取文章
    @RequestMapping("/chat", produces = ["text/html;charset=utf-8"])
    fun readArticle(@RequestBody article: String, prompt: String, articleId: Long, title: String): Flux<String> {
        logger.info("文章id: $articleId -> chat: $prompt")
        return articleChatClient
            .prompt()
            // 仅将问题存入记忆，文章内容作为临时上下文
            .user(prompt)
            // 在advisors中传递文章内容作为临时参数，而非记忆内容
            .advisors { a ->
                a.params(mapOf(
                    ChatMemory.CONVERSATION_ID to articleId.toString(),
                    ArticleAdvisor.ARTICLE_CONTEXT to article,  // 作为临时上下文参数
                    ArticleAdvisor.ARTICLE_TITLE to title
                ))
            }
            .stream()
            .content()
    }

    // 删除所有聊天
    @DeleteMapping
    fun deleteAll(): Result<Unit> {
        articleAiMemoryService.remove(null)
        return Result.success()
    }

    // 根据文章id删除聊天
    @DeleteMapping("/articleId/{articleId}")
    fun deleteByArticleId(@PathVariable articleId: Long): Result<Unit> {
        logger.info("删除文章id为{}的聊天", articleId)
        articleAiMemoryService.removeByArticleId(articleId)
        return Result.success()
    }

    // 根据id删除聊天
    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): Result<Unit> {
        logger.info("删除id为{}的聊天", id)
        articleAiMemoryService.removeById(id)
        return Result.success()
    }

    // 根据文章id获取聊天
    @GetMapping("articleId/{articleId}")
    fun getChatByArticleId(@PathVariable articleId: Long): Result<List<SpringAiChatMemory>> {
        logger.info("获取文章id为{}的聊天", articleId)
        val chatMemories = articleAiMemoryService.listByArticleId(articleId)
        return Result.success(chatMemories)
    }

    // 根据id获取聊天
    @GetMapping("/{id}")
    fun getChat(@PathVariable id: Long): Result<SpringAiChatMemory> {
        val chatMemory = articleAiMemoryService.getById(id)
        return Result.success(chatMemory)
    }

    // 下载聊天记录（md格式）
    @GetMapping("/download/{id}")
    fun downloadChat(@PathVariable id: Long): ResponseEntity<Resource> {
        val markdownFile = articleAiMemoryService.downloadChatByArticle(id)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/markdown; charset=UTF-8")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"chat_${id}_${LocalDate.now()}.md\"")
            .body(FileSystemResource(markdownFile))
    }
}
