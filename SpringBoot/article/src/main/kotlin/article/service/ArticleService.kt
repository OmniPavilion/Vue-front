package article.service

import article.exception.ArticleException
import article.pojo.dto.ArticleQuery
import article.pojo.vo.ArticleVO
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity

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
    fun getArticlePage(pageDTO: PageDTO<ArticleQuery>): PageVO<ArticleVO>

    @Throws(ArticleException::class)
    fun getArticleFile(id: Long) :  String

    @Throws(ArticleException::class)
    fun updateArticleFile(id: Long, file: String)

    @Throws(ArticleException::class)
    fun updateRoot(path: String)

    @Throws(ArticleException::class)
    fun getRootPath(): String

    @Throws(ArticleException::class)
    fun downloadAllArticles(): Resource
}