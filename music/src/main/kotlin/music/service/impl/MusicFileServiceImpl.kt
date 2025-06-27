package music.service.impl

import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.exception.FileException
import common.utils.MultipartFileUtils
import music.constant.MusicConstant
import music.constant.MusicRedisConstant
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.service.MusicFileService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File

@Service
@Datasource(DataSourceType.MUSIC)
class MusicFileServiceImpl(
    private val musicConstant: MusicConstant,
    private val stringRedisTemplate: StringRedisTemplate,
    private val musicMapper: MusicMapper,
    private val singerMapper: SingerMapper
) : MusicFileService {

    @Transactional
    override fun updateRoot(path: String) {
        if (path.isEmpty()) {
            throw FileException("路径不能为空")
        }

        val oldSingerRootPath = musicConstant.rootPath

        if (path == oldSingerRootPath) {
            throw FileException("路径不能相同")
        }

        MultipartFileUtils.moveFolder(oldSingerRootPath, path)

        stringRedisTemplate.opsForHash<String, String>()
            .put(MusicRedisConstant.FILE_KEY, MusicRedisConstant.ROOT_FIELD, path)
        musicConstant.init()


    }

    override fun getMusicFile(id: Int): File {
        val music = musicMapper.selectById(id)
        val singer = singerMapper.selectById(music.singerId)

        return MultipartFileUtils.getFile("${musicConstant.musicRootPath}${singer.name}/${music.fileName}")
    }
}