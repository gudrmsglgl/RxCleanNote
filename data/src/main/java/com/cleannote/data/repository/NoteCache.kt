package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteCache {
    fun getNumNotes(): Flowable<List<NoteEntity>>
    fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long>
    fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>>
    fun saveNotes(notes: List<NoteEntity>): Completable
    fun isCached(page: Int): Single<Boolean>
    fun setLastCacheTime(lastCache: Long, page: Int = 1)
}