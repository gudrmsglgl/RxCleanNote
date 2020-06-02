package com.cleannote.presentation.mapper

import com.cleannote.domain.model.Note
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.util.DateUtil
import java.util.*
import javax.inject.Inject

open class NoteMapper @Inject constructor(
    private val dateUtil: DateUtil
): Mapper<NoteView, Note>{

    override fun mapToView(type: Note): NoteView = NoteView(
        type.id, type.title, type.body, type.updated_at, type.created_at
    )

    override fun mapFromTitle(title: String): Note = Note(
        id = UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = dateUtil.getCurrentTimestamp(),
        updated_at = dateUtil.getCurrentTimestamp()
    )
}