import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName
data class Weather(
    @TableId
    val id: Int? = null,
    val name: String
)