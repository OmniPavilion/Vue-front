package common.service

import com.baomidou.mybatisplus.extension.service.IService
import common.pojo.po.SpringAiChatMemory
import java.io.File

interface AiMemoryService: IService<SpringAiChatMemory> {
    fun removeByConversationId(conversationId: Long)
    fun listByConversationId(conversationId: Long): List<SpringAiChatMemory>
    fun downloadChat(conversationId: Long): File
    fun downloadChatByArticle(conversationId: Long): File

}