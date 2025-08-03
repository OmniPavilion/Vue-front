package article.service.impl

import article.constant.ArticleConstant
import article.constant.ArticleRedisConstant
import article.exception.ArticleException
import article.exception.TagException
import article.mapper.ArticleCategoryMapper
import article.mapper.ArticleMapper
import article.mapper.TagMapper
import article.pojo.dto.ArticleQuery
import article.pojo.po.Article
import article.pojo.po.ArticleTag
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
    private val tagMapper: TagMapper,
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
        saveArticleCategories(po.id!!, article.tagIds)

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
            KtQueryWrapper(ArticleTag::class.java)
                .eq(ArticleTag::articleId, id)
        ).map { it.tagId }

        return ArticleVO(
            id = article.id,
            title = article.title,
            fileName = article.fileName,
            weather = article.weather,
            writtenAt = article.writtenAt,
            tagIds = categoryIds
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
        val queryWrapper = KtQueryWrapper(ArticleTag::class.java)
            .eq(ArticleTag::articleId, article.id)
        articleCategoryMapper.delete(queryWrapper)
        saveArticleCategories(article.id, article.tagIds)
    }

    @Transactional
    override fun deleteArticle(id: Long) {
        val existing = articleMapper.selectById(id)

        if (existing == null) {
            throw ArticleException("未找到ID为${id}的文章")
        }

        // 先删除关联关系
        val queryWrapper = KtQueryWrapper(ArticleTag::class.java)
            .eq(ArticleTag::articleId, id)
        articleCategoryMapper.delete(queryWrapper)

        if (articleMapper.deleteById(id) != 1) {
            throw ArticleException("删除文章失败")
        }

        // 删除文件
        val path = "${articleConstant.rootPath}/${existing.fileName}"
        MultipartFileUtils.deleteFile(path)
    }

    override fun getArticlePage(pageDTO: PageDTO<ArticleQuery>): PageVO<ArticleVO> {
        val page = Page<Article>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val queryWrapper = KtQueryWrapper(Article::class.java).apply {
           pageDTO.query?.let { query ->
               query.title?.let {
                   like(Article::title, query.title)
               }
               query.startTime?.let {
                   ge(Article::writtenAt, query.startTime)
               }
               query.endTime?.let {
                   le(Article::writtenAt, query.endTime)
               }
           }
            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Article::writtenAt)
                SortDirection.DESC -> orderByDesc(Article::writtenAt)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = articleMapper.selectPage(page, queryWrapper)

        // 筛选不满足分类的文章
        val articleTagList = articleCategoryMapper.selectList(
            KtQueryWrapper(ArticleTag::class.java).apply {
                pageDTO.query?.tagId?.let {
                    eq(ArticleTag::tagId, it)
                }
            }
        )

        result.records = result.records.filter { article ->
            articleTagList.any { it.articleId == article.id }
        }

        // 转换为VO列表
        val voList = result.records.map { article ->
            val categoryIds = articleCategoryMapper.selectList(
                KtQueryWrapper(ArticleTag::class.java)
                    .eq(ArticleTag::articleId, article.id)
            ).map { it.tagId }

            ArticleVO(
                id = article.id,
                title = article.title,
                fileName = article.fileName,
                weather = article.weather,
                writtenAt = article.writtenAt,
                tagIds = categoryIds
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
        val existingTagIds = tagMapper.selectBatchIds(categoryIds).map { it.id }.toSet()
        val nonExistingTags = categoryIds.filterNot { existingTagIds.contains(it) }

        if (nonExistingTags.isNotEmpty()) {
            throw TagException("以下标签ID不存在: ${nonExistingTags.joinToString()}")
        }

        if (categoryIds.isNotEmpty()) {
            val relations = categoryIds.map { categoryId ->
                ArticleTag(
                    articleId = articleId,
                    tagId = categoryId,
                    createdAt = LocalDateTime.now()
                )
            }
            articleCategoryMapper.insert(relations)
        }
    }
}