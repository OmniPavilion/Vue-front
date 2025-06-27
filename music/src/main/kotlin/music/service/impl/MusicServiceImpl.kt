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
import music.constant.MusicRedisConstant
import music.exception.MusicException
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.mapper.CategoryMapper
import music.pojo.dto.MusicQuery
import music.pojo.po.Category
import music.pojo.po.Music
import music.pojo.po.Singer
import music.pojo.vo.MusicVO
import music.service.MusicService
import music.utils.AudioDurationUtil
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.MUSIC)
class MusicServiceImpl(
    private val musicMapper: MusicMapper,
    private val singerMapper: SingerMapper,
    private val categoryMapper: CategoryMapper,
    private val stringRedisTemplate: StringRedisTemplate,
    private val resourceFileUtils: MultipartFileUtils
) : MusicService {

    // 常用音乐后缀
    private val musicExtensions = listOf("mp3", "flac", "wav", "aac", "ogg", "m4a")

    override fun getMusicPage(pageDTO: PageDTO<MusicQuery>): PageVO<MusicVO> {
        val page = Page<Music>(pageDTO.pageNum.toLong(), pageDTO.pageSize.toLong())
        val query = pageDTO.query ?: MusicQuery()

        val wrapper = KtQueryWrapper(Music::class.java).apply {
            query.title?.let { like(Music::title, it) }
            query.singerId?.let { eq(Music::singerId, it) }
            query.categoryId?.let { eq(Music::categoryId, it) }
            query.isFavorite?.let { eq(Music::isFavorite, it) }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Music::id)
                SortDirection.DESC -> orderByDesc(Music::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = musicMapper.selectPage(page, wrapper)
        val musicVOList = result.records.map { toVO(it) }
        return PageVO(result.total, musicVOList)
    }

    @Transactional(readOnly = true)
    override fun getMusicById(id: Int): MusicVO {
        val music = musicMapper.selectById(id) ?: throw MusicException("id为${id}的音乐不存在")
        return toVO(music)
    }

    @Transactional
    override fun createMusic(multipartFile: MultipartFile, singer: String, category: String) {
        val singerWrapper = KtQueryWrapper(Singer::class.java).apply {
            eq(Singer::name, singer)
        }

        val selectSingerOne = singerMapper.selectOne(singerWrapper)
        var singerId = selectSingerOne?.id
        if (selectSingerOne == null) {
            val newSinger = Singer(name = singer)
            singerMapper.insert(newSinger)
            singerId = newSinger.id
        }

        val categoryWrapper = KtQueryWrapper(Category::class.java).apply {
            eq(Category::name, category)
        }
        val selectCategoryOne = categoryMapper.selectOne(categoryWrapper)
        var categoryId = selectCategoryOne?.id
        if (selectCategoryOne == null) {
            val newCategory = Category(name = category)
            categoryMapper.insert(newCategory)
            categoryId = newCategory.id
        }

        val file = resourceFileUtils.getFileByMultipartFile(multipartFile)

        // 获取后缀
        val extension = file.name.substringAfterLast(".")
        // 文件名（不包含后缀）
        val name = file.name.substringBeforeLast(".")

        //  判断文件是否为音乐文件
        if (!musicExtensions.contains(extension)) {
            throw MusicException(
                "仅支持以下音频格式: ${musicExtensions.joinToString()}\n" +
                        "不支持的文件格式: ${file.name}"
            )
        }

        val title = name
        // 当前时间戳为文件名
        val fileName = System.currentTimeMillis().toString() + "." + extension
        // 获取文件大小
        val size = file.length()
        // 获取音乐时长
        val duration = AudioDurationUtil.getDuration(file)

        val music = Music(
            title = title,
            fileName = fileName,
            fileSize = size,
            duration = duration.toInt(),
            singerId = singerId,
            categoryId = categoryId,
            isFavorite = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        if (musicMapper.insert(music) != 1) {
            throw MusicException("创建音乐失败")
        }


        val rootPath = stringRedisTemplate.opsForHash<String, String>()
                .get(MusicRedisConstant.FILE_KEY, MusicRedisConstant.MUSIC_ROOT_FIELD)
        resourceFileUtils.addFile(file, "$rootPath$singer/$fileName")
    }

    @Transactional
    override fun updateMusic(musicVO: MusicVO) {
        if (musicVO.id == 0) throw MusicException("音乐ID不能为空")

        val existing = musicMapper.selectById(musicVO.id) ?: throw MusicException("id为${musicVO.id}的音乐不存在")
        validateMusic(musicVO)

        val singer = musicVO.singerName?.let { singerMapper.selectById(it) }
        if (singer == null) {
            throw MusicException("歌手不存在")
        }

        val category = musicVO.categoryName?.let { categoryMapper.selectById(it) }
        if (category == null) {
            throw MusicException("分类不存在")
        }


        existing.apply {
            title = musicVO.title
            isFavorite = musicVO.isFavorite
            singerId = singer.id
            categoryId = category.id
            updatedAt = LocalDateTime.now()
        }

        if (musicMapper.updateById(existing) != 1) {
            throw MusicException("更新音乐失败")
        }
    }

    @Transactional
    override fun deleteMusic(id: Int) {
        if (id == 0) throw MusicException("音乐ID不能为空")

        musicMapper.selectById(id) ?: throw MusicException("id为${id}的音乐不存在")

        if (musicMapper.deleteById(id) != 1) {
            throw MusicException("删除音乐失败")
        }
    }

    @Transactional
    override fun incrementPlayCount(id: Int) {
        val wrapper = KtUpdateWrapper(Music::class.java).apply {
            setSql("play_count = play_count + 1")
            set(Music::lastPlayed, Instant.now())
            eq(Music::id, id)
        }
        if (musicMapper.update(null, wrapper) != 1) {
            throw MusicException("更新播放次数失败")
        }
    }

    @Transactional
    override fun toggleFavorite(id: Int): Boolean {
        val music = musicMapper.selectById(id) ?: throw MusicException("音乐不存在")
        val newStatus = !music.isFavorite

        val wrapper = KtUpdateWrapper(Music::class.java).apply {
            set(Music::isFavorite, newStatus)
            eq(Music::id, id)
        }

        if (musicMapper.update(null, wrapper) != 1) {
            throw MusicException("更新收藏状态失败")
        }
        return newStatus
    }

    private fun validateMusic(musicVO: MusicVO) {
        if (musicVO.title.isBlank()) throw MusicException("音乐标题不能为空")
        if (musicVO.fileName.isBlank()) throw MusicException("文件名不能为空")
        if (musicVO.duration < 0) throw MusicException("时长不能为负数")
    }

    private fun toVO(music: Music): MusicVO {
        return MusicVO(
            id = music.id,
            title = music.title,
            fileName = music.fileName,
            fileSize = music.fileSize,
            duration = music.duration,
            playCount = music.playCount,
            lastPlayed = music.lastPlayed,
            isFavorite = music.isFavorite,
            createdAt = music.createdAt,
            updatedAt = music.updatedAt,
            singerName = music.singerId?.let { singerMapper.selectById(it)?.name },
            categoryName = music.categoryId?.let { categoryMapper.selectById(it)?.name }
        )
    }
}