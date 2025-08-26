package article.service.impl

import article.service.ArticleAiMemoryService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.mapper.AiMemoryMapper
import common.pojo.po.SpringAiChatMemory
import common.service.impl.AiMemoryServiceImpl
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

@Service
@Datasource(DataSourceType.ARTICLE)
class ArticleAiMemoryServiceImpl(
    private val aiMemoryMapper: AiMemoryMapper
): ArticleAiMemoryService, AiMemoryServiceImpl(aiMemoryMapper)