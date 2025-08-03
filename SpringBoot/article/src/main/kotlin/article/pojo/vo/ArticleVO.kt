package article.pojo.vo

import com.baomidou.mybatisplus.annotation.TableId
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ArticleVO(
    @TableId
    val id: Long?,
    val title: String,
    val fileName: String,
    val weather: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val writtenAt: LocalDateTime,
    val categoryIds: List<Long> = emptyList() // 关联分类ID列表
)