package music.pojo.dto

data class PlayArg (
    val currentMusicId: Int? = null,
    val playMode: String,
    val volume: Float,
    val playDuration: Int
)