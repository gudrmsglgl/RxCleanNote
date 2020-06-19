package com.cleannote.presentation.mapper

import com.cleannote.domain.model.Note
import com.cleannote.presentation.model.NoteView
import javax.inject.Inject

open class NoteMapper @Inject constructor(): Mapper<NoteView, Note>{

    override fun mapToView(type: Note): NoteView = NoteView(
        type.id, type.title, type.body, type.updated_at, type.created_at
    )

    fun mapFromView(type: NoteView) = Note(
        type.id, type.title, type.body, type.created_at, type.updated_at
    )
}