package com.cleannote.data.extensions

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.NoteImageEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.NoteImage
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User

fun NoteImageEntity.transNoteImage() = NoteImage(this.imgPk, this.notePk, this.imgPath)
fun List<NoteImageEntity>.transNoteImages() = map { it.transNoteImage() }

fun NoteImage.transNoteImageEntity() = NoteImageEntity(this.imgPk, this.notePk, this.imgPath)
fun List<NoteImage>.transNoteImageEntities() = map { it.transNoteImageEntity() }

fun NoteEntity.transNote() = Note(
    this.id,
    this.title,
    this.body,
    this.updatedAt,
    this.createdAt,
    this.images?.transNoteImages()
)

fun Note.transNoteEntity() = NoteEntity(
    this.id,
    this.title,
    this.body,
    this.updatedAt,
    this.createdAt,
    this.images?.transNoteImageEntities()
)

fun List<Note>.transNoteEntityList() = map { it.transNoteEntity() }
fun List<NoteEntity>.transNoteList() = map { it.transNote() }

fun QueryEntity.transQuery() = Query(
    this.page, this.limit, this.sort, this.order, this.like
)

fun Query.transQueryEntity() = QueryEntity(
    this.page, this.limit, this.sort, this.order, this.like, this.startIndex
)

fun UserEntity.transUser() = User(this.userId, this.nickName)
fun User.transUserEntity() = UserEntity(this.userId, this.nickName)
fun List<UserEntity>.transUserList() = map { it.transUser() }
fun List<User>.transUserEntityList() = map { it.transUserEntity() }
