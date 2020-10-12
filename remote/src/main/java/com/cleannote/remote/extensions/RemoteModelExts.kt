package com.cleannote.remote.extensions

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.NoteImageEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.model.UserModel
import java.util.*


fun String.transImageEntity(notePk: String) = NoteImageEntity(UUID.randomUUID().toString(), notePk, this)
fun List<String>.transImageEntities(notePk: String) = map { it.transImageEntity(notePk) }

fun NoteModel.transEntity() = NoteEntity(
    this.id, this.title, this.body, this.updated_at, this.created_at, this.images?.transImageEntities(this.id))
fun List<NoteModel>.transNoteEntities() = this.map { it.transEntity() }

fun UserModel.transEntity() = UserEntity(this.userId, this.nick)
fun List<UserModel>.transUserEntities() = this.map { it.transEntity() }
