package music.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import music.pojo.po.Singer
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SingerMapper : BaseMapper<Singer>