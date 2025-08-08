package article.service

import article.exception.TagException
import article.pojo.po.Tag
import article.pojo.vo.TagVO
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO

interface TagService {
    @Throws(TagException::class)
    fun createTag(tag: TagVO): Long

    @Throws(TagException::class)
    fun getTagById(id: Long): Tag

    @Throws(TagException::class)
    fun updateTag(tag: TagVO)

    @Throws(TagException::class)
    fun deleteTag(id: Long)

    @Throws(TagException::class)
    fun getAllTags(): List<Tag>

    @Throws(TagException::class)
    fun getTagPage(pageDTO: PageDTO<String>): PageVO<Tag>
}