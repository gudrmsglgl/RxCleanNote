package com.cleannote.data.test.factory

import com.cleannote.data.model.NoteEntity
import com.cleannote.domain.model.Note
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    val dateFormat: SimpleDateFormat = SimpleDateFormat()

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createNoteEntity(
        id: String? = null,
        title: String,
        body: String? = null
    ): NoteEntity = NoteEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createNote(
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

    fun createNoteEntityList(count: Int): List<NoteEntity> = (0 until count).map {
        createNoteEntity("#$it", "title #it", "body #it")}.toList()

    fun createNoteList(count: Int): List<Note> = (0 until count).map {
        createNote("#$it", "title #it", "body #it")}.toList()

}