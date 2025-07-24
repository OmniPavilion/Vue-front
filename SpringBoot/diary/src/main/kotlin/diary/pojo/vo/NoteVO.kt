package diary.pojo.vo

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonFormat
import diary.pojo.po.Status
import java.time.LocalDate
import java.time.LocalTime

@TableName
data class NoteVO(
    @TableId
    val id: Int?,
    val content: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dueDate: LocalDate,
    @JsonFormat(pattern = "HH:mm:ss")
    val dueTime: LocalTime,
    val status: Status,
    val notes: String?
)