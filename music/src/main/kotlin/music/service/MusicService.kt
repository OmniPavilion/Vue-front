package music.service

import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import music.exception.MusicException
import music.pojo.dto.MusicQuery
import music.pojo.vo.MusicVO
import org.springframework.web.multipart.MultipartFile

interface MusicService {
    @Throws(MusicException::class)
    fun getMusicPage(pageDTO: PageDTO<MusicQuery>): PageVO<MusicVO>
    @Throws(MusicException::class)
    fun getMusicById(id: Int): MusicVO
    @Throws(MusicException::class)
    fun createMusic(multipartFile: MultipartFile, singer: String, category: String)
    @Throws(MusicException::class)
    fun updateMusic(musicVO: MusicVO)
    @Throws(MusicException::class)
    fun deleteMusic(id: Int)
    @Throws(MusicException::class)
    fun incrementPlayCount(id: Int)
    @Throws(MusicException::class)
    fun toggleFavorite(id: Int): Boolean
    @Throws(MusicException::class)
    fun deleteMusics(ids: IntArray)
    @Throws(MusicException::class)
    fun getNextMusic(id: Int, mode: String, isNext:  Boolean, query: MusicQuery): MusicVO
    @Throws(MusicException::class)
    fun getMusicPosition(id: Int, pageDTO: PageDTO<MusicQuery>): Int
}