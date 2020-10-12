package com.cleannote.presentation.extensions

import com.cleannote.domain.model.Note
import com.cleannote.domain.model.NoteImage
import com.cleannote.domain.model.User
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.model.UserView

fun NoteImage.transNoteImageView() = NoteImageView(this.img_pk, this.note_pk, this.img_path)
fun List<NoteImage>.transNoteImageViews() = map { it.transNoteImageView() }

fun NoteView.transNote() = Note(this.id, this.title, this.body, this.updated_at, this.created_at)
fun List<NoteView>.transNotes() = map { it.transNote() }

fun Note.transNoteView() = NoteView(this.id, this.title, this.body, this.updated_at, this.created_at, this.images?.transNoteImageViews())
fun List<Note>.transNoteViews() = map { it.transNoteView() }

fun User.transUserView() = UserView(this.userId, this.nickName)
fun List<User>.transUserView() = map { it.transUserView() }