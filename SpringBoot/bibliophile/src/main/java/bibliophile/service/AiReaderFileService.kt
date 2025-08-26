package bibliophile.service

import AiReaderFile
import bibliophile.exception.AiReaderFileException
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import org.springframework.web.multipart.MultipartFile

interface AiReaderFileService {
    @Throws(AiReaderFileException::class)
    fun createFile(file: MultipartFile): Long

    @Throws(AiReaderFileException::class)
    fun getFileById(id: Long): AiReaderFile

    @Throws(AiReaderFileException::class)
    fun deleteFile(id: Long)

    @Throws(AiReaderFileException::class)
    fun getAllFiles(): List<AiReaderFile>
}