package article.service

import article.exception.ArticleException
import article.pojo.vo.ArticleVO
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO

interface ArticleService {
    @Throws(ArticleException::class)
    fun createArticle(article: ArticleVO): Long

    @Throws(ArticleException::class)
    fun getArticleById(id: Long): ArticleVO

    @Throws(ArticleException::class)
    fun updateArticle(article: ArticleVO)

    @Throws(ArticleException::class)
    fun deleteArticle(id: Long)

    @Throws(ArticleException::class)
    fun getArticlePage(pageDTO: PageDTO<Unit>): PageVO<ArticleVO>

    @Throws(ArticleException::class)
    fun getArticleFile(id: Long) :  String

    @Throws(ArticleException::class)
    fun updateArticleFile(id: Long, file: String)

    @Throws(ArticleException::class)
    fun updateRoot(path: String)
}