package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.Plan
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PlanMapper : BaseMapper<Plan> {
    @Select("SELECT * FROM t_plan WHERE status = #{status} AND start_date <= CURRENT_DATE AND end_date >= CURRENT_DATE")
    fun selectActivePlans(status: String): List<Plan>
}