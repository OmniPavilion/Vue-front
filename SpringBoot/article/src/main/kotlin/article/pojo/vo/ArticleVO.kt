package article.pojo.vo

import com.baomidou.mybatisplus.annotation.TableId
import java.time.LocalDateTime

data class ArticleVO(
    @TableId
    val id: Long?,
    val title: String,
    val fileName: String,
    val weather: String,
    val writtenAt: LocalDateTime,
    val tagIds: List<Long> = emptyList() // 关联分类ID列表
)