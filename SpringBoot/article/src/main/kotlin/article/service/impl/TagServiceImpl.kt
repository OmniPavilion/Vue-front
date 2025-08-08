package article.service.impl

import article.exception.TagException
import article.mapper.TagMapper
import article.pojo.po.Tag
import article.pojo.vo.TagVO
import article.service.TagService
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.ARTICLE)
class TagServiceImpl(
    private val tagMapper: TagMapper
) : TagService {
    override fun createTag(tag: TagVO): Long {
        val now = LocalDateTime.now()
        val po = Tag(
            name = tag.name,
            createdAt = now,
            updatedAt = now
        )

        if (po.name.isBlank()) {
            throw TagException("分类名称不能为空")
        }

        if (tagMapper.selectCount(
                KtQueryWrapper(Tag::class.java)
                    .eq(Tag::name, po.name)
            ) > 0
        ) {
            throw TagException("分类名称已存在")
        }

        if (tagMapper.insert(po) != 1) {
            throw TagException("创建分类失败")
        }

        return po.id ?: throw TagException("获取分类ID失败")
    }

    override fun getTagById(id: Long): Tag {
        return tagMapper.selectById(id)
            ?: throw TagException("未找到ID为${id}的分类")
    }

    override fun updateTag(tag: TagVO) {
        if (tag.id == null) {
            throw TagException("未指定分类ID")
        }

        val existing = getTagById(tag.id)
        val updated = existing.copy(
            name = tag.name,
            updatedAt = LocalDateTime.now()
        )

        if (updated.name.isBlank()) {
            throw TagException("分类名称不能为空")
        }

        if (existing.name != updated.name) {
            if (tagMapper.selectCount(
                    KtQueryWrapper(Tag::class.java)
                        .eq(Tag::name, updated.name)
                ) > 0
            ) {
                throw TagException("分类名称已存在")
            }
        }

        if (tagMapper.updateById(updated) != 1) {
            throw TagException("更新分类失败")
        }
    }

    override fun deleteTag(id: Long) {
        // 检查是否有关联文章
        val count = tagMapper.selectCount(
            KtQueryWrapper(Tag::class.java)
                .eq(Tag::id, id)
        )

        if (count == 0L) {
            throw TagException("未找到ID为${id}的分类")
        }

        if (tagMapper.deleteById(id) != 1) {
            throw TagException("删除分类失败")
        }
    }

    override fun getAllTags(): List<Tag> {
        return tagMapper.selectList(null)
    }

    override fun getTagPage(pageDTO: PageDTO<String>): PageVO<Tag> {
        val page = Page<Tag>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val queryWrapper = KtQueryWrapper(Tag::class.java).apply {
            if (pageDTO.query != null) {
                like(Tag::name, pageDTO.query)
            }

            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Tag::id)
                SortDirection.DESC -> orderByDesc(Tag::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = tagMapper.selectPage(page, queryWrapper)
        return PageVO(result.total, result.records)
    }

}