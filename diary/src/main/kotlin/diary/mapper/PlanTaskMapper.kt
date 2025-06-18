package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.PlanTask
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PlanTaskMapper : BaseMapper<PlanTask> {
    @Select("SELECT * FROM t_plan_task WHERE plan_id = #{planId} ORDER BY due_date ASC")
    fun selectTasksByPlanId(planId: Int): List<PlanTask>

    @Select("SELECT * FROM t_plan_task WHERE status = 'pending' AND due_date < CURRENT_DATE")
    fun selectOverdueTasks(): List<PlanTask>
}