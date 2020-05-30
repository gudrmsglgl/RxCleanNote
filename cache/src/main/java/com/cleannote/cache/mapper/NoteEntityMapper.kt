package com.cleannote.cache.mapper

import com.cleannote.cache.model.CachedNote
import com.cleannote.data.model.NoteEntity
import javax.inject.Inject

open class NoteEntityMapper @Inject constructor(): EntityMapper<CachedNote, NoteEntity> {
    override fun mapFromCached(type: CachedNote): NoteEntity = NoteEntity(
        type.id, type.title, type.body, type.updated_at, type.created_at
    )

    override fun mapToCached(type: NoteEntity): CachedNote = CachedNote(
        type.id, type.title, type.body, type.updated_at, type.created_at
    )
}