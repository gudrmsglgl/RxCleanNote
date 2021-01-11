package com.cleannote.presentation.extensions

import com.cleannote.domain.model.Note
import com.cleannote.domain.model.NoteImage
import com.cleannote.domain.model.User
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.model.UserView
import java.util.*

fun NoteImageView.transNoteImage() = NoteImage(this.imgPk, this.notePk, this.imagePath)
fun List<NoteImageView>.transNoteImages() = map { it.transNoteImage() }

fun NoteImage.transNoteImageView() = NoteImageView(this.imgPk, this.notePk, this.imgPath)
fun List<NoteImage>.transNoteImageViews() = map { it.transNoteImageView() }

fun NoteView.transNote() = Note(this.id, this.title, this.body, this.updatedAt, this.createdAt, this.noteImages?.transNoteImages())
fun List<NoteView>.transNotes() = map { it.transNote() }

fun Note.transNoteView() = NoteView(this.id, this.title, this.body, this.updatedAt, this.createdAt, this.images?.transNoteImageViews())
fun List<Note>.transNoteViews() = map { it.transNoteView() }

fun User.transUserView() = UserView(this.userId, this.nickName)
fun List<User>.transUserView() = map { it.transUserView() }

fun String.createNoteImageView(notePk: String) = NoteImageView(
    imgPk = UUID.randomUUID().toString(),
    notePk = notePk,
    imagePath = this
)