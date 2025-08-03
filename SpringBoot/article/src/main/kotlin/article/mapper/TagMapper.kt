package article.mapper

import article.pojo.po.Tag
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface TagMapper : BaseMapper<Tag>