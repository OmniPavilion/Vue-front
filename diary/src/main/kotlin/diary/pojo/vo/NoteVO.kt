package diary.pojo.vo

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import diary.pojo.po.Status
import java.time.LocalDateTime

@TableName
data class NoteVO(
    @TableId
    val id: Int?,
    val content: String,
    val dueDate: LocalDateTime,
    val dueTime: String,
    val status: Status,
    val notes: String?
)