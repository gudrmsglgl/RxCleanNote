package com.cleannote.data.mapper

import com.cleannote.data.model.NoteEntity
import com.cleannote.domain.model.Note
import javax.inject.Inject

open class NoteMapper @Inject constructor(): Mapper<NoteEntity, Note> {

    override fun mapFromEntity(type: NoteEntity): Note = Note(
        type.id,
        type.title,
        type.body,
        type.updated_at,
        type.created_at
    )

    override fun mapToEntity(type: Note): NoteEntity = NoteEntity(
        type.id,
        type.title,
        type.body,
        type.updated_at,
        type.created_at
    )

}