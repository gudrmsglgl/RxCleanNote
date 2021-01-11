package com.cleannote.presentation.test.factory

import com.cleannote.presentation.model.NoteView
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss")

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createNoteView(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = NoteView(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?:"",
        createdAt = "2020-07-${date} 12:00:$date",
        updatedAt = "2020-07-${date} 12:00:$date"
    )

    fun oneOfNotesUpdate(notes: List<NoteView>, index: Int, title: String?, body: String?) =
        notes[index]
            .copy(id = notes[index].id, title = title ?: notes[index].title,
                body = body ?: notes[index].body, createdAt = notes[index].createdAt, updatedAt = getCurrentTimestamp())

    fun createNoteViewList(start:Int, count: Int): List<NoteView> = (start until count).map {
        createNoteView("#$it", "title #it", "body #it", it.toString()) }.toList()


}