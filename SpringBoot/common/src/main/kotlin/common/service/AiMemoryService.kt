package common.service

import com.baomidou.mybatisplus.extension.service.IService
import common.pojo.po.SpringAiChatMemory
import org.springframework.stereotype.Service
import java.io.File

interface AiMemoryService: IService<SpringAiChatMemory> {
    fun removeByArticleId(conversationId: Long)
    fun listByArticleId(conversationId: Long): List<SpringAiChatMemory>
    fun downloadChat(conversationId: Long): File
}