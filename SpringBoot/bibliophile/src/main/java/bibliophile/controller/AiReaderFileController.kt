package bibliophile.controller

import AiReaderFile
import bibliophile.service.AiReaderFileAiMemoryService
import bibliophile.service.AiReaderFileService
import common.pojo.vo.Result
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/bibliophile/files")
class AiReaderFileController(
    private val aiReaderFileService: AiReaderFileService,
    private val aiReaderFileAiMemoryService: AiReaderFileAiMemoryService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createFile(@RequestParam file: MultipartFile): Result<Long> {
        logger.info { "创建文件记录: $file" }
        val id = aiReaderFileService.createFile(file)
        return Result.success(id)
    }

    @GetMapping("/{id}")
    fun getFile(@PathVariable id: Long): Result<AiReaderFile> {
        logger.info { "获取文件记录: $id" }
        val file = aiReaderFileService.getFileById(id)
        return Result.success(file)
    }

    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除文件记录: $id" }
        aiReaderFileService.deleteFile(id)
        aiReaderFileAiMemoryService.deleteVectorStoreByFileId(id)
        aiReaderFileAiMemoryService.removeByConversationId(id)
        return Result.success()
    }

    @GetMapping
    fun getAllFiles(): Result<List<AiReaderFile>> {
        logger.info { "获取全部文件记录" }
        val files = aiReaderFileService.getAllFiles()
        return Result.success(files)
    }
}