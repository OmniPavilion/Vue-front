package article.pojo.dto

import java.time.LocalDateTime

data class ArticleQuery (
    val title: String? = null,
    val tagId: Long? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null
)