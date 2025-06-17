import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.Date

@TableName
data class Log(
    @TableId
    val id: Int? = null,
    val recordDate: Date = Date(),
    val createTime: Date? = null,
    val activity: String,
    val categoryId: Int?,
    val weatherId: Int = 1
)