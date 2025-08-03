package article.service.impl

import article.constant.ArticleConstant
import article.constant.ArticleRedisConstant
import article.exception.ArticleException
import article.mapper.ArticleCategoryMapper
import article.mapper.ArticleMapper
import article.pojo.po.Article
import article.pojo.po.ArticleCategory
import article.pojo.vo.ArticleVO
import article.service.ArticleService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.exception.FileException
import common.utils.MultipartFileUtils
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.ARTICLE)
class ArticleServiceImpl(
    private val articleMapper: ArticleMapper,
    private val articleCategoryMapper: ArticleCategoryMapper,
    private val articleConstant: ArticleConstant,
    private val stringRedisTemplate: StringRedisTemplate,
) : ArticleService {
    @Transactional
    override fun createArticle(article: ArticleVO): Long {
        // 时间戳
        val timestamp = System.currentTimeMillis()

        val now = LocalDateTime.now()
        val po = Article(
            title = article.title,
            fileName = "$timestamp.md",
            weather = article.weather,
            writtenAt = article.writtenAt,
            createdAt = now,
            updatedAt = now
        )

        if (articleMapper.insert(po) != 1) {
            throw ArticleException("创建文章失败")
        }

        // 处理分类关联
        saveArticleCategories(po.id!!, article.categoryIds)

        // 创建空文件
        val path = "${articleConstant.rootPath}/${po.fileName}"

        MultipartFileUtils.createEmptyFile(path)

        return po.id
    }

    override fun getArticleById(id: Long): ArticleVO {
        val article = articleMapper.selectById(id)
            ?: throw ArticleException("未找到ID为${id}的文章")

        // 查询关联分类
        val categoryIds = articleCategoryMapper.selectList(
            KtQueryWrapper(ArticleCategory::class.java)
                .eq(ArticleCategory::articleId, id)
        ).map { it.categoryId }

        return ArticleVO(
            id = article.id,
            title = article.title,
            fileName = article.fileName,
            weather = article.weather,
            writtenAt = article.writtenAt,
            categoryIds = categoryIds
        )
    }

    @Transactional
    override fun updateArticle(article: ArticleVO) {
        if (article.id == null) {
            throw ArticleException("未指定文章ID")
        }

        val existing = articleMapper.selectById(article.id)
            ?: throw ArticleException("未找到ID为${article.id}的文章")

        val updated = existing.copy(
            title = article.title,
            weather = article.weather,
            writtenAt = article.writtenAt,
            updatedAt = LocalDateTime.now()
        )

        if (articleMapper.updateById(updated) != 1) {
            throw ArticleException("更新文章失败")
        }

        // 更新分类关联
        val queryWrapper = KtQueryWrapper(ArticleCategory::class.java)
            .eq(ArticleCategory::articleId, article.id)
        articleCategoryMapper.delete(queryWrapper)
        saveArticleCategories(article.id, article.categoryIds)
    }

    @Transactional
    override fun deleteArticle(id: Long) {
        val existing = articleMapper.selectById(id)

        if (existing == null) {
            throw ArticleException("未找到ID为${id}的文章")
        }

        // 先删除关联关系
        val queryWrapper = KtQueryWrapper(ArticleCategory::class.java)
            .eq(ArticleCategory::articleId, id)
        articleCategoryMapper.delete(queryWrapper)

        if (articleMapper.deleteById(id) != 1) {
            throw ArticleException("删除文章失败")
        }

        // 删除文件
        val path = "${articleConstant.rootPath}/${existing.fileName}"
        MultipartFileUtils.deleteFile(path)
    }

    override fun getArticlePage(pageDTO: PageDTO<Unit>): PageVO<ArticleVO> {
        val page = Page<Article>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val queryWrapper = KtQueryWrapper(Article::class.java).apply {
            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Article::id)
                SortDirection.DESC -> orderByDesc(Article::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = articleMapper.selectPage(page, queryWrapper)

        // 转换为VO列表
        val voList = result.records.map { article ->
            val categoryIds = articleCategoryMapper.selectList(
                KtQueryWrapper(ArticleCategory::class.java)
                    .eq(ArticleCategory::articleId, article.id)
            ).map { it.categoryId }

            ArticleVO(
                id = article.id,
                title = article.title,
                fileName = article.fileName,
                weather = article.weather,
                writtenAt = article.writtenAt,
                categoryIds = categoryIds
            )
        }

        return PageVO(result.total, voList)
    }

    override fun getArticleFile(id: Long): String {
        val article = articleMapper.selectById(id)
            ?: throw ArticleException("未找到ID为${id}的文章")

        val path = "${articleConstant.rootPath}/${article.fileName}"

        val readFileToString = MultipartFileUtils.readFileToString(path)

        return readFileToString
    }

    override fun updateArticleFile(id: Long, file: String) {
        val article = articleMapper.selectById(id)
            ?: throw ArticleException("未找到ID为${id}的文章")

        val path = "${articleConstant.rootPath}/${article.fileName}"

        MultipartFileUtils.writeStringToFile(path, file)
    }

    @Transactional
    override fun updateRoot(path: String) {
        if (path.isEmpty()) {
            throw FileException("路径不能为空")
        }


        val oldSingerRootPath = articleConstant.rootPath

        if (path == oldSingerRootPath ) {
            val defaultPath = stringRedisTemplate.opsForHash<String, String>()
                .get(ArticleRedisConstant.FILE_KEY, ArticleRedisConstant.DEFAULT_ROOT_FIELD)
                ?: throw FileException("请先设置默认音乐根目录")

            if (path == defaultPath) return

            throw FileException("路径不能相同")
        }

        // 禁止将文件夹移动到其自身子目录
        if (path.startsWith(oldSingerRootPath)) {
            throw FileException("请勿将文件夹移动到其自身子目录")
        }

        MultipartFileUtils.moveFolder(oldSingerRootPath, path)

        stringRedisTemplate.opsForHash<String, String>()
            .put(ArticleRedisConstant.FILE_KEY, ArticleRedisConstant.ROOT_FIELD, path)
        articleConstant.init()
    }

    private fun saveArticleCategories(articleId: Long, categoryIds: List<Long>) {
        if (categoryIds.isNotEmpty()) {
            val relations = categoryIds.map { categoryId ->
                ArticleCategory(
                    articleId = articleId,
                    categoryId = categoryId,
                    createdAt = LocalDateTime.now()
                )
            }
            articleCategoryMapper.insert(relations)
        }
    }
}