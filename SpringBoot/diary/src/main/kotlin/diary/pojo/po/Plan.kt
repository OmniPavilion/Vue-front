package diary.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@TableName
data class Plan(
    @TableId
    val id: Int,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)