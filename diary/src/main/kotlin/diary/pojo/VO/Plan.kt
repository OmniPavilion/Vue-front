import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.Date

@TableName
data class Plan(
    @TableId
    val id: Int? = null,
    val title: String,
    val startDate: Date,
    val endDate: Date,
    val status: String = "draft",
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)