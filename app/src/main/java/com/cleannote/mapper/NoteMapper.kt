package com.cleannote.mapper

import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.model.NoteView
import javax.inject.Inject

open class NoteMapper @Inject constructor(): Mapper<NoteUiModel, NoteView> {
    override fun mapToUiModel(type: NoteView): NoteUiModel =
        NoteUiModel(type.id, type.title, type.body, type.updated_at, type.created_at)
}