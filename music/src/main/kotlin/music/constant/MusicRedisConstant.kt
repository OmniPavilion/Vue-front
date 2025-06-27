package music.constant

class MusicRedisConstant {
    companion object {
        const val MUSIC_KEY = "music:"

        const val PLAY_KEY = MUSIC_KEY + "play:"
        const val FILE_KEY = MUSIC_KEY + "file:"

        // 文件根路径
        const val MUSIC_ROOT_FIELD = "music_root_path"
        const val SINGER_ROOT_FIELD = "singer_root_path"
        // 当前音乐
        const val CURRENT_MUSIC_FIELD = "current_music"
        // 音量
        const val VOLUME_FIELD = "volume"
        // 播放模式
        const val PLAY_MODE_FIELD = "play_mode"
        // 播放总时长
        const val PLAY_DURATION_FIELD = "play_duration"
    }
}