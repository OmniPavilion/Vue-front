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

    @GetMapping("/page")
    fun getMusicPage(@RequestBody pageDTO: PageDTO<MusicQuery>): Result<PageVO<MusicVO>> {
        logger.info { " 分页查询参数: ${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        return Result.success(musicService.getMusicPage(pageDTO))
    }

    @GetMapping("/{id}")
    fun getMusicById(@PathVariable id: Int): Result<MusicVO> {
        logger.info { " 查询参数: $id" }
        return Result.success(musicService.getMusicById(id))
    }

    @PostMapping
    fun createMusic(@RequestParam file: MultipartFile,
                    @RequestParam singer : String,
                    @RequestParam category : String
    ): Result<Unit> {
        logger.info { " 音乐参数: $file" }
        logger.info { " 分类参数: $singer, $category" }
        musicService.createMusic(file, singer, category)
        return Result.success()
    }

    @PutMapping
    fun updateMusic(@RequestBody musicVO: MusicVO): Result<Unit> {
        musicService.updateMusic(musicVO)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteMusic(@PathVariable id: Int): Result<Unit> {
        logger.info { " 删除参数: $id" }
        musicService.deleteMusic(id)
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
}