package music.service

import org.springframework.web.multipart.MultipartFile

interface SingerPictureService {
    fun uploadPicture(singerId: Long, multipartFile: MultipartFile)
    fun getPicture(singerId: Long, pictureId: Long?): ByteArray?
    fun deletePicture(singerId: Long, pictureId: Long)
    fun getPictures(singerId: Long): List<ByteArray?>
}