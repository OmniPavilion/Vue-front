package music.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.utils.MultipartFileUtils
import music.constant.MusicConstant
import music.constant.MusicRedisConstant
import music.exception.MusicException
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.pojo.po.Music
import music.pojo.po.Singer
import music.pojo.vo.SingerVO
import music.service.SingerService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Paths
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.MUSIC)
class SingerServiceImpl(
    private val singerMapper: SingerMapper,
    private val musicMapper: MusicMapper,
    private val musicConstant: MusicConstant
) : SingerService {


    override fun getSingerPage(pageDTO: PageDTO<String>): PageVO<SingerVO> {
        val page = Page<Singer>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val wrapper = KtQueryWrapper(Singer::class.java).apply {
            pageDTO.query?.let {
                like(Singer::name, it)
            }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Singer::id)
                SortDirection.DESC -> orderByDesc(Singer::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = singerMapper.selectPage(page, wrapper)
        val singerVOList = result.records.map { singer ->
            SingerVO(
                id = singer.id,
                name = singer.name,
            )
        }
        return PageVO(result.total, singerVOList)
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
    override fun createSinger(singerVO: SingerVO) {
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

        // 判断文件夹是否存在
        val musicPath = musicConstant.musicRootPath + singer.name
        if (Paths.get(musicPath).toFile().exists()) {
            MultipartFileUtils.renameFolder(musicPath, singerVO.name)
        }

        val singerPath = musicConstant.musicRootPath + singer.name
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

        singerMapper.selectById(id)
            ?: throw MusicException("歌手不存在")

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