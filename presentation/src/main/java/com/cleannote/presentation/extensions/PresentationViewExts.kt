package com.cleannote.presentation.extensions

import com.cleannote.domain.model.Note
import com.cleannote.domain.model.User
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.model.UserView

fun NoteView.transNote() = Note(this.id, this.title, this.body, this.updated_at, this.created_at)
fun List<NoteView>.transNotes() = map { it.transNote() }

fun Note.transNoteView() = NoteView(this.id, this.title, this.body, this.updated_at, this.created_at)
fun List<Note>.transNoteViews() = map { it.transNoteView() }

fun User.transUserView() = UserView(this.userId, this.nickName)
fun List<User>.transUserView() = map { it.transUserView() }