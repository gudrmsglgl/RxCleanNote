package com.cleannote.remote.mapper

import com.cleannote.data.model.NoteEntity
import com.cleannote.remote.model.NoteModel
import javax.inject.Inject

open class NoteEntityMapper @Inject constructor(): EntityMapper<NoteModel, NoteEntity>{
    override fun mapFromRemote(type: NoteModel): NoteEntity = NoteEntity(
        type.id, type.title, type.body, type.updated_at, type.created_at
    )
}