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
        createdAt = getCurrentTimestamp(),
        updatedAt = getCurrentTimestamp()
    )

    fun createNote(
        id: String? = null,
        title: String,
        body: String? = null
    ): Note = Note(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        createdAt = getCurrentTimestamp(),
        updatedAt = getCurrentTimestamp()
    )

    fun createNoteEntityList(start:Int, count: Int): List<NoteEntity> = (start until count).map {
        createNoteEntity("#$it", "title #it", "body #it")}.toList()

    fun createNoteList(start:Int, count: Int): List<Note> = (start until count).map {
        createNote("#$it", "title #it", "body #it")}.toList()

}