package music.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import music.pojo.po.Category
import org.apache.ibatis.annotations.Mapper

@Mapper
interface CategoryMapper : BaseMapper<Category>