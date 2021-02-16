package com.cleannote.test

import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.model.Note
import com.cleannote.model.NoteUiModel
import java.util.*

object NoteFactory {


    fun makeNote(
        id: String? = null,
        title: String,
        body: String? = null,
        date: String
    ) = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        createdAt = "2020-07-${date} 12:00:$date",
        updatedAt = "2020-07-${date} 12:00:$date"
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
        createdAt = "2020-07-${date} 12:00:$date",
        updatedAt = "2020-07-${date} 12:00:$date"
    )

    fun makeNotes(size: Int, order: String): List<Note> =
        if (order == ORDER_ASC)
            (0 until size).map { makeNote(title = "$it TestTitle", date = it.toString()) }
        else
            (size-1 downTo 0).map { makeNote(title = "$it TestTitle", date = it.toString()) }


}