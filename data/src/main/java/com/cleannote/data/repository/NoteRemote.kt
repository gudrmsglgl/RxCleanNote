package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteRemote {
    fun insertRemoteNewNote(noteEntity: NoteEntity): Completable
    fun login(userId: String): Flowable<List<UserEntity>>
    fun searchNotes(queryEntity: QueryEntity): Single<List<NoteEntity>>
    fun updateNote(noteEntity: NoteEntity): Completable
}
