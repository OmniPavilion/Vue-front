package music.pojo.vo

import java.time.Instant
import java.time.LocalDateTime

data class MusicVO(
    val id: Int = 0,
    val title: String,
    val fileName: String,
    val fileSize: Long = 0,
    val duration: Int = 0,
    val playCount: Int = 0,
    val lastPlayed: Instant? = null,
    val isFavorite: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val singerName: String? = "",
    val categoryName: String? = "",
    val url : String,
    val pictureUrl: String
)