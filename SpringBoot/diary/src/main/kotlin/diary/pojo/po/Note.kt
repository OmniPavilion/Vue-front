package diary.pojo.po


import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@TableName
data class Note(
    @TableId
    val id: Int = 0,
    val content: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime,
    val status: Status = Status.DRAFT,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val notes: String?
)