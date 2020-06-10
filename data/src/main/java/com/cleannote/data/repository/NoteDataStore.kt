package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteDataStore {
    fun getNumNotes(): Flowable<List<NoteEntity>>
    fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long>
    fun insertRemoteNewNote(noteEntity: NoteEntity): Completable
    fun login(userId: String): Flowable<List<UserEntity>>
}