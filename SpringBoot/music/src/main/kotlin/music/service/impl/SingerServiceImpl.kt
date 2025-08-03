package music.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.annotation.Datasource
import common.constant.InternetConstant
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.utils.MultipartFileUtils
import music.constant.FIlePathConstant.Companion.SINGER_IMAGES_STATIC_PATH
import music.constant.MusicConstant
import music.exception.MusicException
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.mapper.SingerPictureMapper
import music.pojo.po.Music
import music.pojo.po.Singer
import music.pojo.po.SingerPicture
import music.pojo.vo.SingerVO
import music.service.SingerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Paths
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.MUSIC)
class SingerServiceImpl(
    private val singerMapper: SingerMapper,
    private val singerPictureMapper: SingerPictureMapper,
    private val musicMapper: MusicMapper,
    private val musicConstant: MusicConstant,
    private val pictureMapper: SingerPictureMapper,
    internetConstant: InternetConstant
) : SingerService {
    val url = internetConstant.url


    override fun getSingerPage(pageDTO: PageDTO<String>, isContainDefaultSinger: Boolean): PageVO<SingerVO> {
        val page = Page<Singer>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val wrapper = KtQueryWrapper(Singer::class.java).apply {
            pageDTO.query?.let {
                like(Singer::name, it)
            }
            if (!isContainDefaultSinger) {
                ne(Singer::id, 1)
            }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Singer::id)
                SortDirection.DESC -> orderByDesc(Singer::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = singerMapper.selectPage(page, wrapper)
        val singerVOList = result.records.map { singer ->
            val wrapper = KtQueryWrapper(SingerPicture::class.java).apply {
                eq(SingerPicture::singerId, singer.id)
                ne(SingerPicture::singerId, 1L)
            }


            val selectList: List<SingerPicture> = singerPictureMapper.selectList(wrapper)

            var path = url + SINGER_IMAGES_STATIC_PATH + singer.name + "/"

            val finalList = selectList.ifEmpty {
                val defaultPicture = singerPictureMapper.selectOne(
                    KtQueryWrapper(SingerPicture::class.java).apply {
                        eq(SingerPicture::singerId, 1)
                        last("ORDER BY RAND() LIMIT 1") // 随机获取一条默认图片
                    }
                )
                path = url + SINGER_IMAGES_STATIC_PATH + musicConstant.DEFAULTPATH
                defaultPicture?.let { listOf(it) } ?: emptyList() // 如果获取到默认图片则使用，否则保持空列表
            }


            val pictureMap = finalList.associate { it.id to path + it.fileName }

            SingerVO(
                id = singer.id,
                name = singer.name,
                pictureMap = pictureMap
            )
        }
        return PageVO(result.total, singerVOList)
    }

    override fun getSingerNames(): Map<Long, String>? {
        return singerMapper.selectList(KtQueryWrapper(Singer::class.java).ne(Singer::id, 1L))
            .associateBy { it.id }
            .mapValues { it.value.name }
    }

    @Transactional
    override fun getSingerById(id: Long): SingerVO {
        val singer = singerMapper.selectById(id)
            ?: throw MusicException("找不到ID为${id}的歌手")

        return SingerVO(
            id = singer.id,
            name = singer.name,
        )
    }

    @Transactional
    override fun createSinger(singerVO: SingerVO): Long {
        if (singerVO.name.isBlank()) {
            throw MusicException("歌手名称不能为空")
        }

        val exists = singerMapper.selectOne(
            KtQueryWrapper(Singer::class.java).apply {
                eq(Singer::name, singerVO.name)
            }
        )

        if (exists != null) {
            throw MusicException("歌手名称已存在")
        }

        val singer = Singer(
            name = singerVO.name,
        )

        val inserted = singerMapper.insert(singer)
        if (inserted != 1) {
            throw MusicException("创建歌手失败")
        }

        return singer.id
    }

    @Transactional
    override fun updateSinger(singerVO: SingerVO) {
        if (singerVO.id == 1L) {
            throw MusicException("无法修改默认歌手")
        }

        if (singerVO.id == 0L) {
            throw MusicException("歌手ID不能为空")
        }

        val singer = singerMapper.selectById(singerVO.id)
            ?: throw MusicException("找不到ID为${singerVO.id}的歌手")

        val wrapper = KtUpdateWrapper(Singer::class.java).apply {
            eq(Singer::name, singerVO.name)
            ne(Singer::id, singerVO.id)
        }
        val singerList = singerMapper.selectList(wrapper)
        if (singerList.isNotEmpty() && singerList.first().id != singer.id) {
            throw MusicException("歌手名称已存在")
        }

        // 判断文件夹是否存在
        val musicPath = musicConstant.musicRootPath + singer.name
        if (Paths.get(musicPath).toFile().exists()) {
            MultipartFileUtils.renameFolder(musicPath, singerVO.name)
        }

        val singerPath = musicConstant.singerRootPath + singer.name
        if (Paths.get(singerPath).toFile().exists()) {
            MultipartFileUtils.renameFolder(singerPath, singerVO.name)
        }

        singer.name = singerVO.name
        singer.updatedAt = LocalDateTime.now()

        val updated = singerMapper.updateById(singer)
        if (updated != 1) {
            throw MusicException("更新歌手失败")
        }
    }

    @Transactional
    override fun deleteSinger(id: Long) {
        if (id == 1L) {
            throw MusicException("无法删除默认歌手")
        }

        if (id == 0L) {
            throw MusicException("歌手ID不能为空")
        }

        val singer = singerMapper.selectById(id)
            ?: throw MusicException("歌手不存在")

        // 判断歌手是否有图片
        if (singerPictureMapper.selectCount(KtQueryWrapper(SingerPicture::class.java).apply {
                eq(SingerPicture::singerId, id)
            }) > 0) {
            val singerPath = musicConstant.singerRootPath + singer.name
            MultipartFileUtils.deleteFolder(singerPath)
        }

        // 删除图片数据库数据
        singerPictureMapper.delete(KtQueryWrapper(SingerPicture::class.java).apply {
            eq(SingerPicture::singerId, id)
        })

        // 修改歌手下的所有歌曲为默认
        val wrapper = KtUpdateWrapper(Music::class.java).apply {
            set(Music::singerId, 1)
            eq(Music::singerId, id)
        }
        musicMapper.update(wrapper)

        val deleted = singerMapper.deleteById(id)
        if (deleted != 1) {
            throw MusicException("删除歌手失败")
        }
    }
}