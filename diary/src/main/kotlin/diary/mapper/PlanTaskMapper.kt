package diary.mapper

import PlanTask
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PlanTaskMapper : BaseMapper<PlanTask> {
    @Select("SELECT * FROM t_plan_tasks WHERE plan_id = #{planId} ORDER BY due_date ASC")
    fun selectTasksByPlanId(planId: Int): List<PlanTask>

    @Select("SELECT * FROM t_plan_tasks WHERE status = 'pending' AND due_date < CURRENT_DATE")
    fun selectOverdueTasks(): List<PlanTask>
}