// LogController.kt
package diary.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.Result
import diary.pojo.dto.LogQuery
import diary.pojo.vo.DailyLogVO
import diary.service.LogService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/logs")
class LogController(private val logService: LogService) {
    private val log = KotlinLogging.logger {}

    @PostMapping("/insert")
    fun createLog(@RequestBody dailyLogVO: DailyLogVO): Result<out Any> {
        log.info { "创建日志：${JSON.toJSONString(dailyLogVO, SerializerFeature.PrettyFormat)}" }
        val id = logService.createLog(dailyLogVO)
        return Result.success(id)

    }

    @DeleteMapping("/{id}")
    fun deleteLogById(@PathVariable id: Int): Result<Unit> {
        log.info { "删除日志：$id" }
        logService.deleteLogById(id)
        return Result.success()

    }

    @PutMapping("/update")
    fun updateLog(@RequestBody dailyLogVO: DailyLogVO): Result<Unit> {
        log.info { "更新日志：${JSON.toJSONString(dailyLogVO, SerializerFeature.PrettyFormat)}" }
        logService.updateLog(dailyLogVO)
        return Result.success()
    }

    @GetMapping("/{id}")
    fun getLogById(@PathVariable id: Int): Result<out Any> {
        log.info { "获取日志：$id" }
        val log = logService.getLogById(id)
        return Result.success(log)

    }

    @PostMapping("/page")
    fun getLogsByPage(@RequestBody pageDTO: PageDTO<LogQuery>): Result<out Any> {
        log.info { "分页查询日志：${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val pageVO = logService.getLogsByPage(pageDTO)
        return Result.success(pageVO)
    }
}