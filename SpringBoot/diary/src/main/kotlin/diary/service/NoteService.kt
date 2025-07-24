// NoteService.kt
package diary.service

import common.pojo.dto.PageDTO
import common.pojo.vo.PageVO
import diary.exception.NoteException
import diary.pojo.po.Note
import diary.pojo.po.Status
import diary.pojo.vo.NoteVO

interface NoteService {
    @Throws(NoteException::class)
    fun createNote(note: NoteVO)

    @Throws(NoteException::class)
    fun getNoteById(id: Int): Note

    @Throws(NoteException::class)
    fun updateNote(note: NoteVO)

    @Throws(NoteException::class)
    fun deleteNote(id: Int)

    @Throws(NoteException::class)
    fun getNotesByStatus(status: Status): List<Note>

    @Throws(NoteException::class)
    fun getNotePage(pageDTO: PageDTO<Status?>): PageVO<Note>
}