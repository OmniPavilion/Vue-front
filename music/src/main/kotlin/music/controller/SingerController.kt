package music.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.pojo.vo.SingerVO
import music.service.SingerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/singers")
class SingerController(private val singerService: SingerService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/page")
    fun getSingers(@RequestBody pageDTO: PageDTO<String>,
                   @RequestParam isContainDefaultSinger:  Boolean): Result<PageVO<SingerVO>> {
        logger.info { "获取歌手列表(分页)：${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val singerPage = singerService.getSingerPage(pageDTO, isContainDefaultSinger)
        return Result.success(singerPage)
    }

    @GetMapping("/list")
    fun getSingerNames(): Result<Map<Long, String>?> {
        logger.info { "获取歌手列表" }
        val singers = singerService.getSingerNames()
        return Result.success(singers)
    }

    @GetMapping("/{id}")
    fun getSingerById(@PathVariable id: Long): Result<SingerVO> {
        logger.info { "获取歌手详情：$id" }
        val singer = singerService.getSingerById(id)
        return Result.success(singer)
    }

    @PostMapping
    fun createSinger(@RequestBody singerVO: SingerVO): Result<Long> {
        logger.info { "创建歌手：${JSON.toJSONString(singerVO, SerializerFeature.PrettyFormat)}" }
        val id = singerService.createSinger(singerVO)
        return Result.success(id)
    }

    @PutMapping
    fun updateSinger(@RequestBody singerVO: SingerVO): Result<Unit> {
        logger.info { "更新歌手：${JSON.toJSONString(singerVO, SerializerFeature.PrettyFormat)}" }
        singerService.updateSinger(singerVO)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deleteSinger(@PathVariable id: Long): Result<Unit> {
        logger.info { "删除歌手：$id" }
        singerService.deleteSinger(id)
        return Result.success()
    }
}