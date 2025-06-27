package music.service.impl

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import music.exception.MusicException
import music.mapper.CategoryMapper
import music.mapper.MusicMapper
import music.pojo.po.Category
import music.pojo.po.Music
import music.pojo.vo.CategoryVO
import music.service.CategoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Datasource(DataSourceType.MUSIC)
class CategoryServiceImpl(
    private val categoryMapper: CategoryMapper,
    private val musicMapper: MusicMapper


) : CategoryService {

    /**
     * 分页获取所有分类
     */
    @Transactional
    override fun getCategoryPage(pageDTO: PageDTO<String>): PageVO<CategoryVO> {
        val page = Page<Category>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val wrapper = KtQueryWrapper(Category::class.java).apply {
            pageDTO.query?.let {
                like(Category::name, it)
            }


            // 排序
            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Category::id)
                SortDirection.DESC -> orderByDesc(Category::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = categoryMapper.selectPage(page, wrapper)
        val categoryVOList = result.records.map {
            CategoryVO(
                id = it.id,
                name = it.name
            )
        }
        val pageVO = PageVO(result.total, categoryVOList)
        return pageVO
    }

    /**
     * 根据ID获取分类
     */
    @Transactional
    override fun getCategoryById(id: Long): CategoryVO {
        val category = categoryMapper.selectById(id)
            ?: throw MusicException("找不到ID为${id}的分类")

        val categoryVO = CategoryVO(
            id = category.id,
            name = category.name
        )
        return categoryVO
    }

    /**
     * 创建新分类
     */
    @Transactional
    override fun createCategory(categoryVO: CategoryVO) {
        if (categoryVO.name.isBlank()) {
            throw MusicException("分类名称不能为空")
        }

        val exists = categoryMapper.selectOne(
            KtQueryWrapper(Category::class.java).apply {
                eq(Category::name, categoryVO.name)
            }
        )

        if (exists != null) {
            throw MusicException("分类名称已存在")
        }

        val category = Category(
            name = categoryVO.name
        )

        val inserted = categoryMapper.insert(category)
        if (inserted != 1) {
            throw MusicException("创建分类失败")
        }
        return
    }

    /**
     * 更新分类信息
     */
    @Transactional
    override fun updateCategory(categoryVO: CategoryVO) {
        if (categoryVO.id == 1L) {
            throw MusicException("静止修改默认分类")
        }

        if (categoryVO.id == 0L) {
            throw MusicException("分类ID不能为空")
        }
        if (categoryVO.name.isBlank()) {
            throw MusicException("分类名称不能为空")
        }

        val category = categoryMapper.selectById(categoryVO.id)
            ?: throw MusicException("找不到ID为${categoryVO.id}的分类")

        category.name = categoryVO.name
        category.updatedAt = LocalDateTime.now()
        val updated = categoryMapper.updateById(category)
        if (updated != 1) {
            throw MusicException("更新分类失败")
        }
        return
    }

    /**
     * 删除分类
     */
    @Transactional
    override fun deleteCategory(id: Long) {
        if (id == 1L) {
            throw MusicException("静止删除默认分类")
        }

        val exists = categoryMapper.selectById( id)

        if (exists == null) {
            throw MusicException("分类名称不存在")
        }

        // 修改该分类下的所有歌曲为默认分类
        val wrapper = KtUpdateWrapper(Music::class.java).apply {
            set(Music::categoryId, 1)
            eq(Music::categoryId, id)
        }
        musicMapper.update(wrapper)

        val deleted = categoryMapper.deleteById(id)
        if (deleted != 1) {
            throw MusicException("删除分类失败")
        }
        return
    }
}