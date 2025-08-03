package article.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class Category(
    @TableId
    val id: Long? = null,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)