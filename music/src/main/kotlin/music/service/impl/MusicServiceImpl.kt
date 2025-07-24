package music.service.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.annotation.Datasource
import common.constant.InternetConstant.Companion.URL
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.utils.MultipartFileUtils
import mu.KotlinLogging
import music.constant.FIlePathConstant.Companion.MUSIC_FILE_STATIC_PATH
import music.constant.FIlePathConstant.Companion.SINGER_IMAGES_STATIC_PATH
import music.constant.MusicConstant
import music.constant.PlayModeConstant
import music.exception.MusicException
import music.mapper.MusicMapper
import music.mapper.SingerMapper
import music.mapper.CategoryMapper
import music.mapper.SingerPictureMapper
import music.pojo.dto.MusicQuery
import music.pojo.po.Category
import music.pojo.po.Music
import music.pojo.po.Singer
import music.pojo.po.SingerPicture
import music.pojo.vo.MusicVO
import music.service.MusicService
import music.utils.AudioDurationUtil
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
    private val resourceFileUtils: MultipartFileUtils,
    private val singerPictureMapper: SingerPictureMapper,
    private val musicConstant: MusicConstant
) : MusicService {

    private val logger = KotlinLogging.logger { }


    // 常用音乐后缀
    private val musicExtensions = listOf("mp3", "flac", "wav", "aac", "ogg", "m4a")

    @Transactional
    override fun getMusicPage(pageDTO: PageDTO<MusicQuery>): PageVO<MusicVO> {
        val page = Page<Music>(pageDTO.pageNum.toLong(), pageDTO.pageSize.toLong())
        val query = pageDTO.query ?: MusicQuery()

        val wrapper = KtQueryWrapper(Music::class.java).apply {
            query.title?.let { like(Music::title, it) }
            query.singerId?.let { if (it != 1) eq(Music::singerId, it) }
            query.categoryId?.let { if (it != 1) eq(Music::categoryId, it) }
            if (query.isFavorite == true) {
                eq(Music::isFavorite, true)
            }

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
        val name = multipartFile.originalFilename!!.substringBeforeLast(".")

        //  判断文件是否为音乐文件
        if (!musicExtensions.contains(extension)) {
            throw MusicException(
                "仅支持以下音频格式: ${musicExtensions.joinToString()}\n" +
                        "不支持的文件格式: ${file.name}"
            )
        }

        val title = name
        logger.info { "上传文件: " }
        logger.info { "上传文件: $title" }
        logger.info { "上传文件: " }

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

        val path = if (music.singerId == 1.toLong()) {
            "${musicConstant.musicRootPath}${musicConstant.DEFAULT}$fileName"
        } else {
            "${musicConstant.musicRootPath}$singer/$fileName"
        }
        resourceFileUtils.addFileWithDeleteBefore(file, path)
    }

    @Transactional
    override fun updateMusic(musicVO: MusicVO) {
        if (musicVO.id == 0) throw MusicException("音乐ID不能为空")

        val existing = musicMapper.selectById(musicVO.id) ?: throw MusicException("id为${musicVO.id}的音乐不存在")
        validateMusic(musicVO)

        val singerWrapper = KtQueryWrapper(Singer::class.java).apply {
            eq(Singer::name, musicVO.singerName)
        }
        val singer = singerMapper.selectOne(singerWrapper)
        if (singer == null) {
            throw MusicException("歌手不存在")
        }

        val categoryWrapper = KtQueryWrapper(Category::class.java).apply {
            eq(Category::name, musicVO.categoryName)
        }
        val category = categoryMapper.selectOne(categoryWrapper)
        if (category == null) {
            throw MusicException("分类不存在")
        }

        val oldSinger = singerMapper.selectById(existing.singerId)
        // 转移音乐文件
        val oldPath = if (existing.singerId == 1L) {
            "${musicConstant.musicRootPath}${musicConstant.DEFAULT}${existing.fileName}"
        } else {
            "${musicConstant.musicRootPath}${oldSinger.name}/${existing.fileName}"
        }

        val newPath = if (singer.id == 1L) {
            "${musicConstant.musicRootPath}${musicConstant.DEFAULT}${existing.fileName}"
        } else {
            "${musicConstant.musicRootPath}${singer.name}/${existing.fileName}"
        }

        if (oldPath != newPath) {
            MultipartFileUtils.moveFile(oldPath, newPath)
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

        val music = musicMapper.selectById(id) ?: throw MusicException("id为${id}的音乐不存在")
        val singer = singerMapper.selectById(music.singerId) ?: throw MusicException("歌手不存在")

        val path = if (music.singerId == 1.toLong()) {
            "${musicConstant.musicRootPath}${musicConstant.DEFAULT}${music.fileName}"
        } else {
            "${musicConstant.musicRootPath}$singer/${music.fileName}"
        }

        MultipartFileUtils.deleteFile(path)

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

    @Transactional
    override fun deleteMusics(ids: IntArray) {
        ids.forEach { id ->
            this.deleteMusic(id)
        }
        return
    }

    @Transactional
    override fun getNextMusic(id: Int, mode: String, isNext: Boolean, query: MusicQuery): MusicVO {
        musicMapper.selectById(id) ?: throw MusicException("当前音乐不存在")

        val wrapper = KtQueryWrapper(Music::class.java).apply {
            query.let { q ->
                q.singerId?.let { eq(Music::singerId, it) }
                q.categoryId?.let { eq(Music::categoryId, it) }
                q.isFavorite?.takeIf { it }?.let { eq(Music::isFavorite, true) }
            }

            when (mode) {
                PlayModeConstant.RANDOM -> last("ORDER BY RAND() LIMIT 1")
                PlayModeConstant.LOOP ->  return toVO(musicMapper.selectById(id) ?: throw MusicException("当前音乐不存在"))

                // 数据库中的歌曲按时按正序排列，但是播放时是按倒序排列的，所以需要倒序
                PlayModeConstant.ORDER -> {
                    if (!isNext) {
                        gt(Music::id, id)
                        orderByAsc(Music::id)
                    } else {
                        lt(Music::id, id)
                        orderByDesc(Music::id)
                    }
                    last("LIMIT 1") // 关键添加
                }

                else -> { // 默认为顺序播放
                    if (isNext) {
                        gt(Music::id, id)
                        orderByAsc(Music::id)
                    } else {
                        lt(Music::id, id)
                        orderByDesc(Music::id)
                    }
                    last("LIMIT 1") // 关键添加
                }
            }
        }

        val nextMusic = musicMapper.selectOne(wrapper)

        // 如果找不到相邻音乐，则循环跳转
        if (nextMusic == null) {
            return toVO(musicMapper.selectById(id) ?: throw MusicException("当前音乐不存在"))
        }

        return toVO(nextMusic)
    }

    override fun getMusicPosition(id: Int, pageDTO: PageDTO<MusicQuery>): Int {
        val wrapper = KtQueryWrapper(Music::class.java).apply {
            pageDTO.query?.let { q ->
                q.singerId?.let { eq(Music::singerId, it) }
                q.categoryId?.let { eq(Music::categoryId, it) }
                q.isFavorite?.takeIf { it }?.let { eq(Music::isFavorite, true) }
            }
            orderByDesc(Music::id)
        }

        val musicList = musicMapper.selectList(wrapper)

        if (musicList.isEmpty()) return 1
        if (!musicList.any { it.id == id }) return 1
        return musicList.indexOfFirst { it.id == id } / pageDTO.pageSize + 1
    }

    private fun validateMusic(musicVO: MusicVO) {
        if (musicVO.title.isBlank()) throw MusicException("音乐标题不能为空")
        if (musicVO.fileName.isBlank()) throw MusicException("文件名不能为空")
        if (musicVO.duration < 0) throw MusicException("时长不能为负数")
    }

    private fun toVO(music: Music): MusicVO {
        val singerName = music.singerId?.let { singerMapper.selectById(it)?.name }
        val categoryName = music.categoryId?.let { categoryMapper.selectById(it)?.name }

        var selectOne = singerPictureMapper.selectOne(
            KtQueryWrapper(SingerPicture::class.java).apply {
                eq(SingerPicture::singerId, music.singerId)
                last("ORDER BY RAND() LIMIT 1") // 随机获取一条默认图片
            })

        selectOne = selectOne
            ?: singerPictureMapper.selectOne(
                KtQueryWrapper(SingerPicture::class.java).apply {
                    eq(SingerPicture::singerId, 1)
                    last("ORDER BY RAND() LIMIT 1") // 随机获取一条默认图片
                })

        val pictureUrl = if (selectOne.singerId == 1L) {

            "$URL$SINGER_IMAGES_STATIC_PATH${musicConstant.DEFAULT}${selectOne.fileName}"
        } else {
            "$URL$SINGER_IMAGES_STATIC_PATH$singerName/${selectOne.fileName}"
        }

        val url = if (music.singerId == 1L) {
            "$URL$MUSIC_FILE_STATIC_PATH${musicConstant.DEFAULT}${music.fileName}"
        } else {
            "$URL$MUSIC_FILE_STATIC_PATH$singerName/${music.fileName}"
        }


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
            singerName = singerName,
            categoryName = categoryName,
            url = url,
            pictureUrl = pictureUrl
        )
    }
}