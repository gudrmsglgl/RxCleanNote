package com.cleannote.cache.test.factory

import com.cleannote.cache.model.CachedNote
import com.cleannote.data.model.NoteEntity
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {

    fun createCachedNote(id: String? = null,
                         title: String,
                         body: String? = null,
                         date: String) = CachedNote(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun createNoteEntity(id: String? = null,
                         title: String,
                         body: String? = null,
                         date: String) = NoteEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        body = body ?: "",
        created_at = "2020-07-${date} 12:00:$date",
        updated_at = "2020-07-${date} 12:00:$date"
    )

    fun createCachedNoteList(start:Int = 0, end: Int): List<CachedNote> =
        (start until end)
            .map {
                createCachedNote("#$it", "title #it", "body #it", it.toString())
            }.toList()

    fun createNoteEntityList(start:Int = 0, end: Int): List<NoteEntity> = (start until end).map {
        createNoteEntity("#$it", "title #it", "body #it", it.toString())}.toList()
}