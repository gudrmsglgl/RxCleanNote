package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteDataStore {
    fun getNumNotes(): Flowable<List<NoteEntity>>
    fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long>
    fun insertRemoteNewNote(noteEntity: NoteEntity): Completable
    fun login(userId: String): Flowable<List<UserEntity>>
    fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>>
    fun saveNotes(notes: List<NoteEntity>, page: Int = 1): Completable
    fun isCached(page: Int): Single<Boolean>
}