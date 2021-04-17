package com.cleannote.remote.test.factory

import com.cleannote.data.model.NoteEntity
import com.cleannote.remote.model.NoteModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

object NoteFactory {
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss")

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createNoteModel(
        id: String? = null,
        title: String,
        body: String? = null,
        images: List<String>? = null
    ) = NoteModel(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        createdAt = getCurrentTimestamp(),
        updatedAt = getCurrentTimestamp(),
        images = images
    )

    fun createNoteEntity(
        id: String? = null,
        title: String,
        body: String? = null
    ) = NoteEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        createdAt = getCurrentTimestamp(),
        updatedAt = getCurrentTimestamp()
    )

    fun createNoteModelList(count: Int): List<NoteModel> = (0 until count).map {
        createNoteModel("#$it", "title #it", "body #it")
    }.toList()

    fun createNoteEntityList(count: Int): List<NoteEntity> = (0 until count).map {
        createNoteEntity("#$it", "title #it", "body #it")
    }.toList()
}
