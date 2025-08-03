package article.mapper

import article.pojo.po.Category
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ArticlesCategoryMapper : BaseMapper<Category>