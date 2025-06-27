package music.utils

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.springframework.core.io.Resource
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object AudioDurationUtil {

    /**
     * 获取音频时长（秒）
     * @param file 临时文件
     * @return 秒数（Double精度）
     */
    fun getDuration(file: File): Double {
        return try {
            val audioFile = AudioFileIO.read(file)
            audioFile.audioHeader.trackLength.toDouble()
        } catch (e: CannotReadException) {
            // 备用方案：通过文件大小估算（仅适用于恒定比特率）
            estimateDuration(file)
        }
    }

    /**
     * 备用方案：估算时长（适用于无法解析元数据的情况）
     */
    private fun estimateDuration(file: File): Double {
        // MP3估算公式：文件大小(byte) / 比特率(kbps) * 8 / 1000
        val assumedBitrate = 128 // 默认128kbps
        return file.length() / (assumedBitrate * 125.0) // 125 = 1000/8
    }
}