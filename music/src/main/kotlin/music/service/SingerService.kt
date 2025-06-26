package music.service

import common.annotation.Datasource
import common.enumerate.DataSourceType
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import music.exception.MusicException
import music.pojo.vo.SingerVO

interface SingerService {
    /**
     * 创建歌手
     * @param singerVO 歌手VO对象
     * @throws MusicException 当创建失败时抛出
     */
    @Throws(MusicException::class)
    fun createSinger(singerVO: SingerVO)

    /**
     * 根据ID获取歌手
     * @param id 歌手ID
     * @return 歌手VO对象
     * @throws MusicException 当歌手不存在时抛出
     */
    @Throws(MusicException::class)
    fun getSingerById(id: Long): SingerVO

    /**
     * 更新歌手信息
     * @param singerVO 歌手VO对象
     * @throws MusicException 当更新失败时抛出
     */
    @Throws(MusicException::class)
    fun updateSinger(singerVO: SingerVO)

    /**
     * 删除歌手
     * @param id 歌手ID
     * @throws MusicException 当删除失败时抛出
     */
    @Throws(MusicException::class)
    fun deleteSinger(id: Long)

    /**
     * 获取歌手分页列表
     * @param pageDTO 分页参数
     * @return 分页结果
     * @throws MusicException 当查询失败时抛出
     */
    @Throws(MusicException::class)
    fun getSingerPage(pageDTO: PageDTO<String>): PageVO<SingerVO>
}