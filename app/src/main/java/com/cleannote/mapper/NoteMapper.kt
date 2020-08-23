package com.cleannote.mapper

import com.cleannote.common.DateUtil
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.model.NoteView
import java.util.*
import javax.inject.Inject

open class NoteMapper @Inject constructor(private val dateUtil: DateUtil): Mapper<NoteUiModel, NoteView> {

    override fun mapToUiModel(type: NoteView): NoteUiModel =
        NoteUiModel(type.id, type.title, type.body, type.updated_at, type.created_at)

    fun mapFromTitle(title: String) =  NoteView(
        id = UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = dateUtil.getCurrentTimestamp(),
        updated_at = dateUtil.getCurrentTimestamp()
    )

    override fun mapToView(type: NoteUiModel): NoteView =
        NoteView(type.id, type.title, type.body, type.updated_at, type.created_at)
}