package diary.pojo.po


import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class Note(
    @TableId
    val id: Int,
    val content: String,
    val dueDate: LocalDateTime,
    val dueTime: String,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notes: String
)