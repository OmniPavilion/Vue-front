package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.Plan
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PlanMapper : BaseMapper<Plan> {
}