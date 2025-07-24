package music.service

import java.io.File

interface MusicFileService {
    fun updateRoot(path: String)
    fun getMusicFile(id: Int): File
}