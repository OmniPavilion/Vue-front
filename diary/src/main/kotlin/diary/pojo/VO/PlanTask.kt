import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.Date

@TableName
data class PlanTask(
    @TableId
    val id: Int? = null,
    val planId: Int,
    val title: String,
    val description: String? = null,
    val dueDate: Date? = null,
    val status: String = "pending"
)