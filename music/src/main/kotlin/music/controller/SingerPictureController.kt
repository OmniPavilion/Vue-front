package music.controller

import common.pojo.vo.Result
import mu.KotlinLogging
import music.service.SingerPictureService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/singers")
class SingerPictureController(
    private val singerPictureService: SingerPictureService
) {

    val logger = KotlinLogging.logger {}

    /**
     * 上传或更新歌手图片
     * PUT /api/singers/{singerId}/picture
     */
    @PutMapping(
        path = ["/{singerId}/pictures"],  // 复数路径更语义化
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadSingerPictures(
        @PathVariable singerId: Long,
        @RequestParam files: Array<MultipartFile>  // 改为接收文件数组
    ): Result<Unit> {
        logger.info { "为歌手 $singerId 批量上传 ${files.size} 张图片" }
        files.forEach { file ->
            singerPictureService.uploadPicture(singerId, file)
        }
        return Result.success()
    }

    /**
     * 获取歌手图片
     * GET /api/singers/{singerId}/picture
     */
    @GetMapping(
        path = ["/{singerId}/picture"],
        produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE]
    )
    fun getSingerPicture(
        @PathVariable singerId: Long,
        @RequestParam pictureId: Long?
    ): ResponseEntity<ByteArray> {
        logger.info { "获取歌手 $singerId 的图片 ${pictureId?: "随机"}" }
        val imageBytes  = singerPictureService.getPicture(singerId, pictureId)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imageBytes)
    }

    /**
     * 获取歌手所有图片
     * GET /api/singers/{singerId}/pictures
     */
    @GetMapping(
        path = ["/{singerId}/pictures"],
        produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE]
    )
    fun getSingerPictures(
        @PathVariable singerId: Long,
    ): ResponseEntity<List<ByteArray?>?> {
        logger.info { "获取歌手 $singerId 所有的图片" }
        val imageBytesArray  = singerPictureService.getPictures(singerId)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imageBytesArray)
    }

    /**
     * 删除歌手图片
     * DELETE /api/singers/{singerId}/picture
     */
    @DeleteMapping("/{singerId}/picture")
    fun deleteSingerPicture(
        @PathVariable singerId: Long,
        @RequestParam pictureId: Long
    ): Result<Unit> {
        logger.info { "删除歌手 $singerId 的图片 $pictureId" }
        singerPictureService.deletePicture(singerId, pictureId)
        return Result.success()
    }
}
