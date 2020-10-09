package com.cleannote.remote.extensions

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.model.UserModel

fun NoteModel.transEntity() = NoteEntity(this.id, this.title, this.body, this.updated_at, this.created_at)
fun List<NoteModel>.transNoteEntities() = this.map { it.transEntity() }

fun UserModel.transEntity() = UserEntity(this.userId, this.nick)
fun List<UserModel>.transUserEntities() = this.map { it.transEntity() }
