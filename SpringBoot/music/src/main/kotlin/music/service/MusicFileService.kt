package music.service

import common.exception.FileException
import java.io.File

interface MusicFileService {
    fun updateRoot(path: String)
    fun getMusicFile(id: Int): File
    @Throws(FileException::class)
    fun checkMusicFile()
}