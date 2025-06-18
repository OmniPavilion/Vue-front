package diary.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import diary.pojo.po.Status
import diary.pojo.vo.PlanVO
import diary.service.PlanService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/plans")
class PlanController(
    private val planService: PlanService
) {

    private var logger = KotlinLogging.logger {  }

    @PostMapping
    fun createPlan(@RequestBody plan: PlanVO): Result<Int> {
        logger.info { "创建计划：${JSON.toJSONString(plan, SerializerFeature.PrettyFormat)}" }
        val id = planService.createPlan(plan)
        return Result.success(id)
    }

    @PutMapping
    fun updatePlan(@RequestBody plan: PlanVO): Result<Unit> {
        logger.info { "更新计划：${JSON.toJSONString(plan, SerializerFeature.PrettyFormat)}" }
        planService.updatePlan(plan)
        return Result.success()
    }

    @DeleteMapping("/{id}")
    fun deletePlan(@PathVariable id: Int): Result<Unit> {
        logger.info { "删除计划：$id" }
        planService.deletePlan(id)
        return Result.success()
    }

    @GetMapping("/{id}")
    fun getPlanById(@PathVariable id: Int): Result<PlanVO> {
        logger.info { "获取计划：$id" }
        val plan = planService.getPlanById(id)
        return Result.success(plan)
    }

    @PostMapping("/page")
    fun getPlansByPage(@RequestBody pageDTO: PageDTO<Status>): Result<PageVO<PlanVO>> {
        logger.info { "获取计划分页：${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val page = planService.getPlansByPage(pageDTO)
        return Result.success(page)
    }
}