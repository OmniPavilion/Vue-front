package article.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime

@TableName
data class ArticleTag (
    @TableId
    val id: Long? = null,
    val articleId: Long,
    val tagId: Long,
    val createdAt: LocalDateTime,
)