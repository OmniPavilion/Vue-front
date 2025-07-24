package diary.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate
import java.time.LocalDateTime

@TableName
data class DailyTime(
    @TableId
    val id: Int,
    val recordDate: LocalDate,
    val weather: Weather,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
    )