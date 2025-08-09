package bibliophile.constant

class AiReaderFileRedisConstant {
    companion object {
        const val AI_READER_FILE_KEY = "ai_reader_file:"

        const val FILE_KEY = AI_READER_FILE_KEY + "file:"

        // 文件根路径
        const val DEFAULT_ROOT_FIELD = "default_root_path"
        const val ROOT_FIELD = "root_path"
    }
}