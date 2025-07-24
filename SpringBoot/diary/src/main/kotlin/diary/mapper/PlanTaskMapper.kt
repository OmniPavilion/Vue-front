package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.PlanTask
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PlanTaskMapper : BaseMapper<PlanTask> {

    @Select("SELECT * FROM t_plan_task WHERE plan_id = #{planId}")
    fun selectByPlanId(planId: Int): List<PlanTask>

    @Delete("DELETE FROM t_plan_task WHERE plan_id = #{planId}")
    fun deleteByPlanId(planId: Int): Int
}