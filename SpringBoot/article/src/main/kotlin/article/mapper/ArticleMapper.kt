package article.mapper

import article.pojo.po.Article
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ArticleMapper : BaseMapper<Article>