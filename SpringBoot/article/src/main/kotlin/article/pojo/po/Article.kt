package article.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class Article(
    @TableId
    val id: Long? = null,
    val title: String,
    val fileName: String,
    val weather: String,
    val writtenAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)