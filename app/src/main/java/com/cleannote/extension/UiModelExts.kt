package com.cleannote.extension

import com.cleannote.model.NoteImageUiModel
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.Default
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import java.text.SimpleDateFormat
import java.util.*

fun List<NoteImageUiModel>.transNoteImageViews() = map { it.transNoteImageView() }
fun NoteImageUiModel.transNoteImageView() = NoteImageView(this.img_pk, this.note_pk, this.img_path)

fun List<NoteImageView>.transNoteImageUiModels() = map { it.transNoteImageUIModel() }
fun NoteImageView.transNoteImageUIModel() = NoteImageUiModel(this.img_pk, this.note_pk, this.img_path)

fun List<NoteView>.transNoteUiModels(mode: NoteMode) = map { it.transNoteUiModel(mode) }
fun NoteView.transNoteUiModel(mode: NoteMode = Default) = NoteUiModel(this.id, this.title, this.body, this.updated_at, this.created_at, mode , this.noteImages?.transNoteImageUiModels())

fun List<NoteUiModel>.transNoteViews() = map { it.transNoteView() }
fun NoteUiModel.transNoteView() =
    NoteView(this.id, this.title, this.body, this.updated_at, this.created_at, this.images?.transNoteImageViews())

fun String.transNoteView() = NoteView(
    id = UUID.randomUUID().toString(),
    title = this,
    body = "",
    created_at = getCurrentTimestamp(),
    updated_at = getCurrentTimestamp()
)

private fun getCurrentTimestamp() = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())