package diary.service.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import common.enumerate.SortDirection
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import diary.exception.NoteException
import diary.mapper.NoteMapper
import diary.pojo.po.Note
import diary.pojo.po.Status
import diary.pojo.vo.NoteVO
import diary.service.NoteService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NoteServiceImpl(private val noteMapper: NoteMapper) : NoteService {
    private val logger = KotlinLogging.logger {}

    // 创建记事
    override fun createNote(note: NoteVO) {
        val now = LocalDateTime.now()
        val note = Note(
            content = note.content,
            dueDate = note.dueDate,
            dueTime = note.dueTime,
            notes = note.notes,
            status = note.status,
            createdAt = now,
            updatedAt = now,
        )

        // 插入失败时抛出异常
        if (noteMapper.insert(note) != 1) {
            throw IllegalStateException("创建记事失败")
        }
    }

    // 根据ID获取记事
    override fun getNoteById(id: Int): Note {
        val selectCount = noteMapper.selectCount(KtQueryWrapper(Note::class.java).eq(Note::id, id))
        if (selectCount == 0L) {
            throw NoteException("未找到ID为${id}的记事")
        }

        return noteMapper.selectById(id)
    }

    // 更新记事
    override fun updateNote(note: NoteVO) {
        if (note.id == null) {
            throw NoteException("未指定ID")
        }

        // 先检查是否存在
        val existingNote = getNoteById(note.id)

        val updatedNote = existingNote.copy(
            content = note.content,
            dueDate = note.dueDate,
            dueTime = note.dueTime,
            status = note.status,
            notes = note.notes,
            updatedAt = LocalDateTime.now()
        )

        // 更新失败时抛出异常
        if (noteMapper.updateById(updatedNote) != 1) {
            throw IllegalStateException("更新记事失败")
        }

        return
    }

    // 删除记事
    override fun deleteNote(id: Int) {
        val selectCount = noteMapper.selectCount(KtQueryWrapper(Note::class.java).eq(Note::id, id))
        if (selectCount == 0L) {
            throw NoteException("未找到ID为${id}的记事")
        }
        noteMapper.deleteById(id)
    }

    // 根据状态获取记事列表
    override fun getNotesByStatus(status: Status): List<Note> {
        return noteMapper.selectList(KtQueryWrapper(Note::class.java).eq(Note::status, status))
    }

    // 获取分页记事
    override fun getNotePage(pageDTO: PageDTO<Status?>): PageVO<Note> {
        val page = Page<Note>(
            pageDTO.pageNum.toLong(),
            pageDTO.pageSize.toLong()
        )

        val queryWrapper = KtQueryWrapper(Note::class.java).apply {
            // 如果查询条件不为空，则添加状态过滤
            pageDTO.query?.let { eq(Note::status, it.id) }

            // 排序处理
            when (pageDTO.order) {
                SortDirection.ASC -> orderByAsc(Note::id)
                SortDirection.DESC -> orderByDesc(Note::id)
                SortDirection.RANDOM -> last("ORDER BY RAND()")
            }
        }

        val result = noteMapper.selectPage(page, queryWrapper)
        return PageVO(result.total, result.records)
    }
}