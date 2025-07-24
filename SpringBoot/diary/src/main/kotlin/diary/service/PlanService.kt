// PlanService.kt
package diary.service

import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import diary.exception.DailyException
import diary.exception.PlanException
import diary.pojo.po.Plan
import diary.pojo.po.Status
import diary.pojo.vo.PlanVO

interface PlanService {
    // 创建新计划
    @Throws(PlanException::class)
    fun createPlan(plan: PlanVO): Int

    // 更新现有计划
    @Throws(PlanException::class)
    fun updatePlan(plan: PlanVO)

    // 删除指定ID的计划
    @Throws(PlanException::class)
    fun deletePlan(id: Int)

    // 获取指定ID的计划详情
    @Throws(PlanException::class)
    fun getPlanById(id: Int): PlanVO

    // 分页获取计划列表
    @Throws(PlanException::class)
    fun getPlansByPage(pageDTO: PageDTO<Status>): PageVO<PlanVO>
}