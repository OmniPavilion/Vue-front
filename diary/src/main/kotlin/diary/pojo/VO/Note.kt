import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.Date

@TableName
data class Note(
    @TableId
    val id: Int? = null,
    val content: String,
    val dueDate: Date? = null,
    val dueTime: String? = null,
    val status: String = "pending",
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val notes: String? = null
)