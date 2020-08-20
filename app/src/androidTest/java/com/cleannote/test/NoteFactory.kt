package com.cleannote.test

import androidx.core.util.rangeTo
import com.cleannote.domain.model.Note
import com.cleannote.model.NoteUiModel
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    val dateFormat: SimpleDateFormat = SimpleDateFormat()

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun makeNote(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun makeNoteUiModel(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = NoteUiModel(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun makeNotes(start: Int, end: Int): List<Note> =
        if (start < end)
            (start until end).map { makeNote(title = "$it TestTitle", date = it.toString()) }
        else
            (start downTo end).map { makeNote(title = "$it TestTitle", date = it.toString()) }


}