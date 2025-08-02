package music.service.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.exception.FileException
import common.utils.MultipartFileUtils
import music.constant.MusicConstant
import music.constant.MusicRedisConstant
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.mapper.SingerPictureMapper
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
    private val singerMapper: SingerMapper,
    private val pictureMapper: SingerPictureMapper
) : MusicFileService {

    @Transactional
    override fun updateRoot(path: String) {
        if (path.isEmpty()) {
            throw FileException("路径不能为空")
        }


        val oldSingerRootPath = musicConstant.rootPath

        if (path == oldSingerRootPath ) {
            val defaultPath = stringRedisTemplate.opsForHash<String, String>()
                .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.DEFAULT_ROOT_FIELD)
                ?: throw FileException("请先设置默认音乐根目录")

            if (path == defaultPath) return

            throw FileException("路径不能相同")
        }

        // 禁止将文件夹移动到其自身子目录
        if (path.startsWith(oldSingerRootPath)) {
            throw FileException("请勿将文件夹移动到其自身子目录")
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

    override fun checkMusicFile() {
        val musicRootPath = musicConstant.musicRootPath
        val singerRootPath = musicConstant.singerRootPath
        val defaultSingerPath = musicConstant.defaultSingerPath
        val default = musicConstant.DEFAULT

        val musicList = musicMapper.selectList(null)
        val singerList = singerMapper.selectList(null)
        val pictureList = pictureMapper.selectList(null)

        // 检查音乐数据库数据是否与文件匹配
        for (music in musicList) {
            var singerName = singerList.firstOrNull { it.id == music.singerId }?.name ?: defaultSingerPath
            if (singerName == "") {
                singerName = default
            }
            val path = "${musicRootPath}${singerName}/${music.fileName}"
            if (!MultipartFileUtils.pathExists(path)) {
                throw FileException("音乐文件不存在\n歌曲名称：${music.title}\n文件路径：${path}")
            }
        }

        // 检查歌手图片数据是否与文件匹配
        for (picture in pictureList) {
            var singerName = singerList.firstOrNull { it.id == picture.singerId }?.name ?: defaultSingerPath
            if (singerName == "") {
                singerName = default
            }
            val path = "${singerRootPath}${singerName}/${picture.fileName}"
            if (!MultipartFileUtils.pathExists(path)) {
                throw FileException("歌手图片文件不存在\n歌手名称：${singerName}\n文件路径：${path}")
            }
        }

        // 检查音乐文件是否冗余
        val musicFileRecords = musicList.associate { music ->
            var singerName = singerList.firstOrNull { it.id == music.singerId }?.name ?: defaultSingerPath
            if (singerName == "") {
                singerName = default
            }
            "${singerName}/${music.fileName}" to true
        }

        MultipartFileUtils.getFilePathsFromFolder(musicRootPath, true)
            .forEach { pathComponents ->
                val relativePath = "${pathComponents[1]}/${pathComponents[0]}" // [0]是文件名，[1]是歌手名
                if (!musicFileRecords.containsKey(relativePath)) {
                    throw FileException("发现冗余音乐文件\n文件路径：${musicRootPath}${relativePath}")
                }
            }

        // 检查歌手图片文件是否冗余
        val pictureFileRecords = pictureList.associate { picture ->
            var singerName = singerList.firstOrNull { it.id == picture.singerId }?.name ?: defaultSingerPath
            if (singerName == "") {
                singerName = default
            }
            "${singerName}/${picture.fileName}" to true
        }

        MultipartFileUtils.getFilePathsFromFolder(singerRootPath, true)
            .forEach { pathComponents ->
                val relativePath = "${pathComponents[1]}/${pathComponents[0]}" // [0]是图片名，[1]是歌手名
                if (!pictureFileRecords.containsKey(relativePath)) {
                    throw FileException("发现冗余歌手图片文件\n文件路径：${singerRootPath}${relativePath}")
                }
            }
    }
}