package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.DailyActivity
import org.apache.ibatis.annotations.Mapper

@Mapper
interface DailyActivityMapper : BaseMapper<DailyActivity> {
}