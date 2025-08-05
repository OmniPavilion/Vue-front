package article.service.impl

import article.service.ArticleAiMemoryService
import common.mapper.AiMemoryMapper
import common.pojo.po.SpringAiChatMemory
import common.service.AiMemoryService
import common.service.impl.AiMemoryServiceImpl
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

@Service
class ArticleAiMemoryServiceImpl(
    private val aiMemoryMapper: AiMemoryMapper
): ArticleAiMemoryService, AiMemoryServiceImpl(aiMemoryMapper) {
    override fun downloadChatByArticle(articleId: Long): File {
        val chats = listByArticleId(articleId)
        val markdownContent = convertToMarkdown(chats)

        return createMarkdownFile(articleId, markdownContent)
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