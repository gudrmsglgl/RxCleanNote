package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteCache
import com.cleannote.data.repository.NoteDataStore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

open class NoteCacheDataStore
@Inject
constructor(
    private val noteCache: NoteCache
): NoteDataStore {

    override fun getNumNotes(): Flowable<List<NoteEntity>> = noteCache.getNumNotes()

    override fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long> = noteCache.insertCacheNewNote(noteEntity)

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun login(userId: String): Flowable<List<UserEntity>> {
        throw UnsupportedOperationException("Not Supported")
    }
}