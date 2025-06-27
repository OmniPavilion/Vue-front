package music.pojo.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.time.LocalDateTime
import java.time.Instant

@TableName
data class Music(
    @TableId
    val id: Int = 0,
    var title: String,
    var fileName: String,
    val fileSize: Long = 0,
    var duration: Int = 0,
    val playCount: Int = 0,
    val lastPlayed: Instant? = null,
    var singerId: Long? = null,
    var categoryId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var isFavorite: Boolean = false,
    var updatedAt: LocalDateTime = LocalDateTime.now()
)