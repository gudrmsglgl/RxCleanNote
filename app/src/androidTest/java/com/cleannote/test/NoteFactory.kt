package com.cleannote.test

import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.model.Note
import com.cleannote.extension.transNoteUiModel
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import java.util.*

object NoteFactory {

    fun defaultNote(): NoteUiModel {
        val notePk = UUID.randomUUID().toString()
        return NoteView(
            id = notePk,
            title = "emptyTile",
            body = "emptyBody",
            updatedAt = "2021-10-10",
            createdAt = "2021-10-10",
            noteImages = listOf(NoteImageView(UUID.randomUUID().toString(), notePk, "https://pbs.twimg.com/profile_images/1105831612132945925/Uf2a_wyY_400x400.jpg"))
        ).transNoteUiModel()
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