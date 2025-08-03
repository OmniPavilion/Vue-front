package article.mapper

import article.pojo.po.ArticleCategory
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ArticleCategoryMapper : BaseMapper<ArticleCategory>