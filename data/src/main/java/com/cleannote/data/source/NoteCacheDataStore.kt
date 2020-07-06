package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
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

    override fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long> =
        noteCache.insertCacheNewNote(noteEntity)

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun login(userId: String): Flowable<List<UserEntity>> {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>> =
        noteCache.searchNotes(queryEntity)

    override fun saveNotes(notes: List<NoteEntity>, page: Int): Completable =
        noteCache.saveNotes(notes, page)
            .doOnComplete {
                noteCache.setLastCacheTime(System.currentTimeMillis(), page)
            }

    override fun isCached(page: Int): Single<Boolean> = noteCache.isCached(page)
}