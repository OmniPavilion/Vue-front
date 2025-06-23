package music.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime
import java.time.Instant

@TableName
data class Music(
    @TableId
    val id: Int = 0,
    val title: String,
    val fileName: String,
    val duration: Int = 0,
    val playCount: Int = 0,
    val lastPlayed: Instant? = null,
    val singerId: Long? = null,
    val categoryId: Long? = null,
    val createTime: LocalDateTime = LocalDateTime.now(),
    val isFavorite: Boolean = false,
    val updateTime: LocalDateTime = LocalDateTime.now()
)