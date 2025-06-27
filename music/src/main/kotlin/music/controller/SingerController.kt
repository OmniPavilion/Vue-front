package music.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import mu.KotlinLogging
import music.pojo.vo.SingerVO
import music.service.SingerService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/singers")
class SingerController(private val singerService: SingerService) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/page")
    fun getSingers(@RequestBody pageDTO: PageDTO<String>): Result<PageVO<SingerVO>> {
        logger.info { "获取歌手列表(分页)：${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val singerPage = singerService.getSingerPage(pageDTO)
        return Result.success(singerPage)
    }

    @GetMapping("/{id}")
    fun getSingerById(@PathVariable id: Long): Result<SingerVO> {
        logger.info { "获取歌手详情：$id" }
        val singer = singerService.getSingerById(id)
        return Result.success(singer)
    }

    @PostMapping
    fun createSinger(@RequestBody singerVO: SingerVO): Result<Unit> {
        logger.info { "创建歌手：${JSON.toJSONString(singerVO, SerializerFeature.PrettyFormat)}" }
        singerService.createSinger(singerVO)
        return Result.success()
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