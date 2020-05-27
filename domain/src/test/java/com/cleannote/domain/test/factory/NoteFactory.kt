package com.cleannote.domain.test.factory

import com.cleannote.domain.model.Note
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {

    val dateFormat: SimpleDateFormat = SimpleDateFormat()

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createSingleNote(
        id: String? = null,
        title: String,
        body: String? = null
    ): Note = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createNoteList(numNotes: Int): List<Note> = (0 until numNotes).map {
        createSingleNote(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )}
        .toList()

}