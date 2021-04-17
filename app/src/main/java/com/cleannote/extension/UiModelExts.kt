package com.cleannote.extension

import com.cleannote.model.NoteImageUiModel
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.Default
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import java.text.SimpleDateFormat
import java.util.*

fun List<NoteImageUiModel>?.transNoteImageViews() = this?.map { it.transNoteImageView() }
fun NoteImageUiModel.transNoteImageView() = NoteImageView(this.imgPk, this.notePk, this.imgPath)

fun List<NoteImageView>?.transNoteImageUiModels() = this?.map { it.transNoteImageUIModel() }
fun NoteImageView.transNoteImageUIModel() = NoteImageUiModel(this.imgPk, this.notePk, this.imagePath)

fun List<NoteView>.transNoteUiModels(mode: NoteMode) = map { it.transNoteUiModel(mode) }
fun NoteView.transNoteUiModel(mode: NoteMode = Default) = NoteUiModel(this.id, this.title, this.body, this.updatedAt, this.createdAt, mode, this.noteImages.transNoteImageUiModels())

fun List<NoteUiModel>.transNoteViews() = map { it.transNoteView() }
fun NoteUiModel.transNoteView() =
    NoteView(this.id, this.title, this.body, this.updatedAt, this.createdAt, this.images.transNoteImageViews())

fun String.transNoteView() = NoteView(
    id = UUID.randomUUID().toString(),
    title = this,
    body = "",
    createdAt = getCurrentTimestamp(),
    updatedAt = getCurrentTimestamp()
)

private fun getCurrentTimestamp() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(
    Calendar.getInstance().time
)
