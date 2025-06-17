import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName
data class Category(
    @TableId
    val id: Int? = null,
    val name: String
)