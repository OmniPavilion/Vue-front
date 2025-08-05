package article.service

import common.service.AiMemoryService
import java.io.File

interface ArticleAiMemoryService : AiMemoryService {
    fun downloadChatByArticle(articleId: Long): File
}