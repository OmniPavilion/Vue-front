package article.pojo.vo

import com.baomidou.mybatisplus.annotation.TableId

data class TagVO(
    @TableId
    val id: Long?,
    val name: String
)