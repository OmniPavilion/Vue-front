package common.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.mapper.AiMemoryMapper
import common.pojo.po.SpringAiChatMemory
import common.service.AiMemoryService
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Datasource(DataSourceType.ARTICLE)
class AiMemoryServiceImpl(
    private val aiMemoryMapper: AiMemoryMapper,
) : ServiceImpl<AiMemoryMapper, SpringAiChatMemory>(), AiMemoryService {
    override fun removeByArticleId(conversationId: Long) {
        aiMemoryMapper.delete(KtQueryWrapper(SpringAiChatMemory::class.java).apply {
            eq(SpringAiChatMemory::conversationId, conversationId.toString())
        })
    }

    override fun listByArticleId(conversationId: Long): List<SpringAiChatMemory> {
        return aiMemoryMapper.selectList(KtQueryWrapper(SpringAiChatMemory::class.java).apply {
            eq(SpringAiChatMemory::conversationId, conversationId.toString())
            orderByAsc(SpringAiChatMemory::timestamp)
        })
    }

    override fun downloadChat(conversationId: Long): File {
        val chatMemory = this.getById(conversationId)
        val markdownContent = chatMemory.content

        // 创建临时文件
        val fileName = "chat_history_${conversationId}_${System.currentTimeMillis()}.md"
        val tempFile = File.createTempFile("chat_", ".md").apply {
            deleteOnExit() // JVM退出时删除临时文件
        }

        // 写入Markdown内容
        tempFile.writeText(markdownContent, Charsets.UTF_8)

        // 重命名为更友好的文件名（可选）
        val renamedFile = File(tempFile.parent, fileName)
        tempFile.renameTo(renamedFile)

        return renamedFile
    }
}