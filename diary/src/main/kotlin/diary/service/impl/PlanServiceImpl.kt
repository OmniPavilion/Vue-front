package diary.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import diary.exception.PlanException
import diary.mapper.PlanMapper
import diary.mapper.PlanTaskMapper
import diary.pojo.po.Plan
import diary.pojo.po.PlanTask
import diary.pojo.po.Status
import diary.pojo.vo.PlanTaskVO
import diary.pojo.vo.PlanVO
import diary.service.PlanService
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PlanServiceImpl(
    private val planMapper: PlanMapper,
    private val planTaskMapper: PlanTaskMapper
) : PlanService {

    @Transactional
    override fun createPlan(plan: PlanVO): Int {
        // 验证计划数据
        validatePlan(plan)

        // 转换VO为PO
        val planPo = Plan(
            id = 0, // ID由数据库自动生成
            title = plan.title,
            startDate = plan.startDate,
            endDate = plan.endDate,
            status = plan.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // 保存计划
        if (planMapper.insert(planPo) <= 0) {
            throw PlanException("创建计划失败")
        }

        // 保存关联任务
        savePlanTasks(planPo.id, plan.tasks)

        return planPo.id
    }

    @Transactional
    override fun updatePlan(plan: PlanVO) {
        // 验证计划数据
        if (plan.id == null) {
            throw PlanException("计划ID不能为空")
        }
        validatePlan(plan)

        // 检查计划是否存在
        val existingPlan = planMapper.selectById(plan.id)
            ?: throw PlanException("计划不存在")

        // 更新计划信息
        val updatedPlan = existingPlan.copy(
            title = plan.title,
            startDate = plan.startDate,
            endDate = plan.endDate,
            status = plan.status,
            updatedAt = LocalDateTime.now()
        )

        if (planMapper.updateById(updatedPlan) <= 0) {
            throw PlanException("更新计划失败")
        }

        // 先删除原有任务，再保存新任务
        planTaskMapper.deleteByPlanId(plan.id)
        savePlanTasks(plan.id, plan.tasks)
    }

    @Transactional
    override fun deletePlan(id: Int) {
        // 删除关联任务
        planTaskMapper.deleteByPlanId(id)

        // 删除计划
        if (planMapper.deleteById(id) <= 0) {
            throw PlanException("删除计划失败")
        }
    }

    override fun getPlanById(id: Int): PlanVO {
        // 获取计划信息
        val plan = planMapper.selectById(id)
            ?: throw PlanException("计划不存在")

        // 获取关联任务
        val tasks = planTaskMapper.selectByPlanId(id)
            .map { task ->
                PlanTaskVO(
                    title = task.title,
                    description = task.description,
                    status = task.status
                )
            }

        // 转换为VO对象
        return PlanVO(
            id = plan.id,
            title = plan.title,
            startDate = plan.startDate,
            endDate = plan.endDate,
            status = plan.status,
            tasks = tasks
        )
    }

    override fun getPlansByPage(pageDTO: PageDTO<Status>): PageVO<PlanVO> {
        // 构建查询条件
        val queryWrapper = KtQueryWrapper(Plan::class.java).apply {
            pageDTO.query?.let { eq(Plan::status, it.id) }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Plan::id)
                SortDirection.DESC -> orderByDesc(Plan::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        // 分页查询
        val page = Page<Plan>(pageDTO.pageNum.toLong(), pageDTO.pageSize.toLong())
        val planPage = planMapper.selectPage(page, queryWrapper)

        // 转换为VO列表
        val planVOs = planPage.records.map { plan ->
            val tasks = planTaskMapper.selectByPlanId(plan.id)
                .map { task ->
                    PlanTaskVO(
                        title = task.title,
                        description = task.description,
                        status = task.status
                    )
                }

            PlanVO(
                id = plan.id,
                title = plan.title,
                startDate = plan.startDate,
                endDate = plan.endDate,
                status = plan.status,
                tasks = tasks
            )
        }

        return PageVO(
            total = planPage.total,
            rows = planVOs
        )
    }

    private fun validatePlan(plan: PlanVO) {
        if (plan.title.isBlank()) {
            throw PlanException("计划标题不能为空")
        }
        if (plan.startDate.isAfter(plan.endDate)) {
            throw PlanException("开始日期不能晚于结束日期")
        }
    }

    private fun savePlanTasks(planId: Int, tasks: List<PlanTaskVO>) {
        tasks.forEach { task ->
            val taskPo = PlanTask(
                id = 0,
                planId = planId,
                title = task.title,
                description = task.description,
                status = task.status
            )

            if (planTaskMapper.insert(taskPo) <= 0) {
                throw PlanException("保存计划任务失败")
            }
        }
    }
}