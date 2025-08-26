package bibliophile.controller

import bibliophile.service.AiReaderFileAiMemoryService
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
import java.time.LocalDate

@RestController
@RequestMapping("/bibliophile/read")
class BibliophileAiReadController(
    private val bibliophileChatClient: ChatClient,
    private val aiReaderFileAiMemoryService: AiReaderFileAiMemoryService
) {
    private val logger = KotlinLogging.logger { }

    // 读取文章
    @RequestMapping("/chat", produces = ["text/html;charset=utf-8"])
    fun readFile(prompt: String, fileId: Long): Flux<String> {
        logger.info(" 文件id: $fileId -> chat: $prompt")
        // 将文件存入向量数据库
        aiReaderFileAiMemoryService.setFileIntoVectorStore(fileId)

        return bibliophileChatClient
            .prompt()
            // 仅将问题存入记忆，文章内容作为临时上下文
            .user("无")
            // 在advisors中传递文章内容作为临时参数，而非记忆内容
            .advisors { a ->
                a.params(mapOf(
                    ChatMemory.CONVERSATION_ID to fileId.toString(),
                ))
            }
            .stream()
            .content()
    }

    // 删除所有聊天
    @DeleteMapping
    fun deleteAll(): Result<Unit> {
        aiReaderFileAiMemoryService.remove(null)
        return Result.success()
    }

    // 根据文章id删除聊天
    @DeleteMapping("/fileId/{fileId}")
    fun deleteByFileId(@PathVariable fileId: Long): Result<Unit> {
        logger.info("删除文章id为{}的聊天", fileId)
        aiReaderFileAiMemoryService.removeByConversationId(fileId)
        return Result.success()
    }

    // 根据id删除聊天
    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): Result<Unit> {
        logger.info("删除id为{}的聊天", id)
        aiReaderFileAiMemoryService.removeById(id)
        return Result.success()
    }

    // 根据文章id获取聊天
    @GetMapping("fileId/{fileId}")
    fun getChatByFileId(@PathVariable fileId: Long): Result<List<SpringAiChatMemory>> {
        logger.info("获取文章id为{}的聊天", fileId)
        val chatMemories = aiReaderFileAiMemoryService.listByConversationId(fileId)
        return Result.success(chatMemories)
    }

    // 根据id获取聊天
    @GetMapping("id/{id}")
    fun getChat(@PathVariable id: Long): Result<SpringAiChatMemory> {
        val chatMemory = aiReaderFileAiMemoryService.getById(id)
        return Result.success(chatMemory)
    }

    // 下载聊天记录（md格式）
    @GetMapping("/download/{id}")
    fun downloadChat(@PathVariable id: Long): ResponseEntity<Resource> {
        val markdownFile = aiReaderFileAiMemoryService.downloadChatByArticle(id)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/markdown; charset=UTF-8")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"chat_${id}_${LocalDate.now()}.md\"")
            .body(FileSystemResource(markdownFile))
    }

    // 清空向量数据库
    @DeleteMapping("/vectorStore")
    fun deleteVectorStore(): Result<Unit> {
        aiReaderFileAiMemoryService.deleteVectorStore()

        return Result.success()
    }
}
