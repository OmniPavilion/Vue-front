// NoteController.kt
package diary.controller

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import common.pojo.vo.Result
import diary.pojo.po.Note
import diary.pojo.po.Status
import diary.pojo.vo.NoteVO
import diary.service.NoteService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notes")
class NoteController(private val noteService: NoteService) {
    private var logger = KotlinLogging.logger {}

    @PostMapping
    fun createNote(@RequestBody note: NoteVO): Result<Unit> {
        logger.info { "添加代办事项: ${JSON.toJSONString(note, SerializerFeature.PrettyFormat)}" }
        noteService.createNote(note)
        return Result.success()

    }

    @GetMapping("/{id}")
    fun getNote(@PathVariable id: Int): Result<Note> {
        logger.info { "获取代办事项: $id" }
        val note = noteService.getNoteById(id)
        return Result.success(note)

    }

    @PutMapping
    fun updateNote(@RequestBody note: NoteVO): Result<Unit> {
        logger.info { "更新代办事项: $note" }
        noteService.updateNote(note)
        return Result.success(Unit)

    }

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: Int): Result<Unit> {
        logger.info { "删除代办事项: $id" }
        noteService.deleteNote(id)
        return Result.success()

    }

    @GetMapping("/status/{status}")
    fun getNotesByStatus(@PathVariable status: Status): Result<List<Note>> {
        logger.info { "查询状态为 $status 的代办事项" }
        val notes = noteService.getNotesByStatus(status)
        return Result.success(notes)

    }

    @PostMapping("/page")
    fun getNotePage(@RequestBody pageDTO: PageDTO<Status?>): Result<PageVO<Note>> {
        logger.info { "分页查询代办事项 ${JSON.toJSONString(pageDTO, SerializerFeature.PrettyFormat)}" }
        val page = noteService.getNotePage(pageDTO)
        return Result.success(page)

    }
}