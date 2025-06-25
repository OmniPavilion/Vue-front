package music.service

import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import music.exception.MusicException
import music.pojo.vo.CategoryVO

interface CategoryService {
    /**
     * 创建分类
     * @param category 分类VO对象
     * @throws MusicException 当创建失败时抛出
     */
    @Throws(MusicException::class)
    fun createCategory(categoryVO: CategoryVO)

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类实体对象
     * @throws MusicException 当分类不存在时抛出
     */
    @Throws(MusicException::class)
    fun getCategoryById(id: Long): CategoryVO

    /**
     * 更新分类信息
     * @param category 分类VO对象
     * @throws MusicException 当更新失败时抛出
     */
    @Throws(MusicException::class)
    fun updateCategory(categoryVO: CategoryVO)

    /**
     * 删除分类
     * @param id 分类ID
     * @throws MusicException 当删除失败时抛出
     */
    @Throws(MusicException::class)
    fun deleteCategory(id: Long)

    /**
     * 获取分类分页列表
     * @param pageDTO 分页参数
     * @return 分页结果
     * @throws MusicException 当查询失败时抛出
     */
    @Throws(MusicException::class)
    fun getCategoryPage(pageDTO: PageDTO<String>): PageVO<CategoryVO>
}