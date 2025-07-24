package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.DailyTime
import org.apache.ibatis.annotations.Mapper

@Mapper
interface DailyTimeMapper : BaseMapper<DailyTime> {
}