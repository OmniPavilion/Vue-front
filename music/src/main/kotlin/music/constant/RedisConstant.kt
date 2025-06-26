package music.constant

class RedisConstant {
    companion object {
        const val PLAY_KEY = "play:"
        const val MUSIC_KEY = "music:"

        // 文件根路径key
        const val ROOT_PATH = MUSIC_KEY + "root_path"
        // 当前音乐
        const val CURRENT_MUSIC = MUSIC_KEY + "current_music"
        // 音量
        const val VOLUME = MUSIC_KEY + "volume"
        // 播放模式
        const val PLAY_MODE = MUSIC_KEY + "play_mode"
        // 播放总时长
        const val PLAY_DURATION = MUSIC_KEY + "play_duration"
    }
}