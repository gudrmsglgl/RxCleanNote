package com.cleannote.presentation.test.factory

import com.cleannote.domain.model.Note
import com.cleannote.presentation.model.NoteView
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss")

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createNote(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun createNoteView(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = NoteView(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun oneOfNotesUpdate(notes: List<NoteView>, index: Int, title: String?, body: String?) =
        notes[index]
            .copy(id = notes[index].id, title = title ?: notes[index].title,
                body = body ?: notes[index].body, created_at = notes[index].created_at, updated_at = getCurrentTimestamp())

    fun createNoteList(start:Int, count: Int): List<Note> = (start until count).map {
        createNote("#$it", "title #it", "body #it", it.toString()) }.toList()

    fun createNoteViewList(start:Int, count: Int): List<NoteView> = (start until count).map {
        createNoteView("#$it", "title #it", "body #it", it.toString()) }.toList()


}