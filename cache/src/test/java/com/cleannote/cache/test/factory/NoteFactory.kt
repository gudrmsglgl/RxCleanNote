package com.cleannote.cache.test.factory

import com.cleannote.cache.model.CachedNote
import com.cleannote.data.model.NoteEntity
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    val dateFormat: SimpleDateFormat = SimpleDateFormat()

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createCachedNote(id: String? = null,
                         title: String,
                         body: String? = null) = CachedNote(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createNoteEntity(id: String? = null,
                         title: String,
                         body: String? = null) = NoteEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createCachedNoteList(count: Int): List<CachedNote> = (0 until count).map {
        createCachedNote("#$it", "title #it", "body #it") }.toList()

    fun createNoteEntityList(count: Int): List<NoteEntity> = (0 until count).map {
        createNoteEntity("#$it", "title #it", "body #it")}.toList()
}