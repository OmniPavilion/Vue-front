package diary.pojo.po


import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDate


@TableName
data class PlanTask(
    @TableId
    val id: Int,
    val planId: Int,
    val title: String,
    val description: String,
    val status: Status
)