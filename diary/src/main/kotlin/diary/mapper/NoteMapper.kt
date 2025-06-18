package diary.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import diary.pojo.po.Note
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Update

@Mapper
interface NoteMapper : BaseMapper<Note> {
    @Update("UPDATE t_note SET status = #{status} WHERE id = #{id}")
    fun updateStatus(id: Int, status: String): Int

    fun selectByStatus(@Param("status") status: String): List<Note>
}