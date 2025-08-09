package common.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import common.mapper.AiMemoryMapper
import common.pojo.po.SpringAiChatMemory
import common.service.AiMemoryService
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

@Service
class AiMemoryServiceImpl(
    private val aiMemoryMapper: AiMemoryMapper,
) : ServiceImpl<AiMemoryMapper, SpringAiChatMemory>(), AiMemoryService {
    override fun removeByConversationId(conversationId: Long) {
        aiMemoryMapper.delete(KtQueryWrapper(SpringAiChatMemory::class.java).apply {
            eq(SpringAiChatMemory::conversationId, conversationId.toString())
        })
    }

    override fun listByConversationId(conversationId: Long): List<SpringAiChatMemory> {
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

    override fun downloadChatByArticle(conversationId: Long): File {
        val chats = listByConversationId(conversationId)
        val markdownContent = convertToMarkdown(chats)

        return createMarkdownFile(conversationId, markdownContent)
    }

    private fun convertToMarkdown(chats: List<SpringAiChatMemory>): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val markdownBuilder = StringBuilder("# 聊天记录\n\n")

        chats.forEach { chat ->
            val role = when (chat.type) {
                "USER" -> "用户"
                "ASSISTANT" -> "AI助手"
                else -> "系统"
            }
            val time = chat.timestamp.format(formatter)

            markdownBuilder.append(
                """
## $role ($time)

${chat.content}

---
---
---

""".trimIndent())
        }

        return markdownBuilder.toString()
    }

    private fun createMarkdownFile(articleId: Long, content: String): File {
        val fileName = "chat_article_${articleId}_${System.currentTimeMillis()}.md"
        val file = File.createTempFile("chat_", ".md").apply {
            deleteOnExit()
        }

        FileWriter(file).use { writer ->
            writer.write(content)
        }

        return file
    }
}