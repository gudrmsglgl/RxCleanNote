package com.cleannote.data.extensions

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User

fun NoteEntity.transNote() = Note(
    this.id,
    this.title,
    this.body,
    this.updated_at,
    this.created_at
)

fun Note.transNoteEntity() = NoteEntity(
    this.id,
    this.title,
    this.body,
    this.updated_at,
    this.created_at
)

fun List<Note>.transNoteEntityList() = map { it.transNoteEntity() }
fun List<NoteEntity>.transNoteList() = map { it.transNote() }

fun QueryEntity.transQuery() = Query(
    this.page, this.limit, this.sort, this.order, this.like
)

fun Query.transQueryEntity() = QueryEntity(
    this.page, this.limit, this.sort, this.order, this.like
)

fun UserEntity.transUser() = User(this.userId, this.nickName)
fun User.transUserEntity() = UserEntity(this.userId, this.nickName)
fun List<UserEntity>.transUserList() = map { it.transUser() }
fun List<User>.transUserEntityList() = map { it.transUserEntity() }