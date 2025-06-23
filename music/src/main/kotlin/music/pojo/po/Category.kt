package music.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class Category(
    @TableId
    val categoryId: Long = 0,
    val name: String,
    val createTime: LocalDateTime = LocalDateTime.now(),
    val updateTime: LocalDateTime = LocalDateTime.now()
)