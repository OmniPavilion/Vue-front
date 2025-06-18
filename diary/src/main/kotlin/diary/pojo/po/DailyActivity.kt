package diary.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class DailyActivity (
    @TableId
    val id: Int,
    val dateId: Int,
    val activity: String,
    val category: Category,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)