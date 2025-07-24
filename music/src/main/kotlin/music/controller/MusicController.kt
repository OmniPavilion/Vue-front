package music.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.pojo.dto.MusicQuery
import music.pojo.vo.MusicVO
import music.service.MusicService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/musics")
class MusicController(private val musicService: MusicService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/page")
    fun getMusicPage(@RequestBody pageDTO: PageDTO<MusicQuery>): Result<PageVO<MusicVO>> {
        logger.info { " 分页查询参数: ${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        return Result.success(musicService.getMusicPage(pageDTO))
    }

    @GetMapping("/{id}")
    fun getMusicById(@PathVariable id: Int): Result<MusicVO> {
        logger.info { " 查询参数: $id" }
        return Result.success(musicService.getMusicById(id))
    }

    @PostMapping("/batch")
    fun createMusics(
        @RequestParam("files") files: Array<MultipartFile>,
        @RequestParam singer: String,
        @RequestParam category: String
    ): Result<Unit> {
        logger.info { "上传音乐文件数量: ${files.size}" }
        logger.info { "歌手: $singer, 分类: $category" }
        logger.info { "文件名: ${files.map { it.originalFilename }}" }

        files.forEach { file ->
            musicService.createMusic(file, singer, category)
        }
        return Result.success()
    }

    @PutMapping
    fun updateMusic(@RequestBody musicVO: MusicVO): Result<Unit> {
        logger.info { " 修改参数: ${JSON.toJSONString(musicVO, SerializerFeature.PrettyFormat)}" }
        musicService.updateMusic(musicVO)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteMusic(@PathVariable id: Int): Result<Unit> {
        logger.info { " 删除参数: $id" }
        musicService.deleteMusic(id)
        return Result.success()
    }

    @DeleteMapping("/batch")
    fun deleteMusics(@RequestBody ids: IntArray): Result<Unit> {
        logger.info { " 批量删除参数: ${JSON.toJSONString(ids, SerializerFeature.PrettyFormat)}" }
        musicService.deleteMusics(ids)
        return Result.success()
    }

    @PostMapping("/{id}/play")
    fun recordPlay(@PathVariable id: Int): Result<Unit> {
        logger.info { " 播放参数: $id" }
        musicService.incrementPlayCount(id)
        return Result.success()
    }

    @PostMapping("/{id}/favorite")
    fun toggleFavorite(@PathVariable id: Int): Result<Boolean> {
        logger.info { " 收藏参数: $id" }
        return Result.success(musicService.toggleFavorite(id))
    }

    @PostMapping("/{id}/next")
    fun getNextMusic(
        @PathVariable id: Int,
        @RequestParam playMode: String,
        @RequestParam isNext : Boolean,
        @RequestBody query: MusicQuery
    ): Result<MusicVO> {
        logger.info { "获取下一首参数: $id, $playMode" }
        return Result.success(musicService.getNextMusic(id,  playMode, isNext, query))
    }


    @PostMapping("/{id}/position")
    fun getMusicPosition(@RequestBody pageDTO: PageDTO<MusicQuery>, @PathVariable id: Int) : Result<Int> {
        logger.info { "获取音乐位置参数: ${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        return Result.success(musicService.getMusicPosition(id, pageDTO))
    }
}