package common.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName("spring_ai_chat_memory")
data class SpringAiChatMemory(
    @TableId
    var id: Long,
    var conversationId: String,
    var content: String,
    var type: String,
    var timestamp: LocalDateTime,
)