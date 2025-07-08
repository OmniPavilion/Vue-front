package music.pojo.vo

import org.springframework.boot.autoconfigure.web.ServerProperties


data class SingerVO(
    val id: Long,
    val name: String,
    val pictureMap : Map<Long, String>? = null,
)