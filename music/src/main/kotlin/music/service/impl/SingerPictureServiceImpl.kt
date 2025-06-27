package music.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.utils.MultipartFileUtils
import mu.KotlinLogging
import music.constant.MusicRedisConstant
import music.exception.MusicException
import music.mapper.SingerMapper
import music.mapper.SingerPictureMapper
import music.pojo.po.SingerPicture
import music.service.SingerPictureService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
@Datasource(DataSourceType.MUSIC)
class SingerPictureServiceImpl(
    private val stringRedisTemplate: StringRedisTemplate,
    private val singerPictureMapper: SingerPictureMapper,
    private val singerMapper: SingerMapper
) : SingerPictureService {

    val logger = KotlinLogging.logger {}

    val rootPath = stringRedisTemplate.opsForHash<String, String>().get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.SINGER_ROOT_FIELD)


    @Transactional
    override fun uploadPicture(singerId: Long, multipartFile: MultipartFile) {
        if (singerId == 1L) {
            throw MusicException("静止为默认歌手添加图片")
        }

        val selectById = singerMapper.selectById(singerId)
        if (selectById == null) {
            throw MusicException("歌手不存在")
        }
        val singName = selectById.name

        // 获取后缀
        val suffix = multipartFile.originalFilename?.substringAfterLast(".")
        // 时间戳为文件名
        val fileName = System.currentTimeMillis().toString() + "." + suffix

        singerPictureMapper.insert(SingerPicture(
            singerId = singerId,
            fileName = fileName
        ))

        val file = MultipartFileUtils.getFileByMultipartFile(multipartFile)
        MultipartFileUtils.addFile(file, "$rootPath$singName/$fileName")
    }

    override fun getPicture(singerId: Long, pictureId: Long?): ByteArray? {
        val singer = singerMapper.selectById(singerId)
        if (singer == null) {
            throw MusicException("歌手不存在")
        }

        val wrapper = KtQueryWrapper(SingerPicture::class.java)
            .eq(SingerPicture::singerId, singerId)
            .last("ORDER BY RAND() LIMIT 1")

        val picture = if (pictureId == null) {
            // 随机获取一张图片
            singerPictureMapper.selectOne(wrapper)
        } else {
            singerPictureMapper.selectById(pictureId)
        }

        if (picture == null || picture.singerId != singerId) {
            throw MusicException("图片不存在")
        }


        return MultipartFileUtils.getFile("$rootPath${singer.name}/${picture.fileName}").readBytes()
    }

    override fun deletePicture(singerId: Long, pictureId: Long) {
        val singer = singerMapper.selectById(singerId)
        if (singer == null) {
            throw MusicException("歌手不存在")
        }

        val picture = singerPictureMapper.selectById(pictureId)
        if (picture == null || picture.singerId != singerId) {
            throw MusicException("图片不存在")
        }

        val fileName = picture.fileName

        singerPictureMapper.deleteById(pictureId)
        MultipartFileUtils.deleteFile("$rootPath${singer.name}/$fileName")
    }
}