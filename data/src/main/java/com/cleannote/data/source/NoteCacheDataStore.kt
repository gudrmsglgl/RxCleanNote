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

    override fun saveNotes(notes: List<NoteEntity>, queryEntity: QueryEntity): Completable =
        noteCache.saveNotes(notes)
            .doOnComplete {
                if (queryEntity.like == null || queryEntity.like == "")
                    noteCache.setLastCacheTime(System.currentTimeMillis(), queryEntity.page)
            }

    override fun isCached(page: Int): Single<Boolean> = noteCache.isCached(page)

    override fun updateNote(noteEntity: NoteEntity): Completable = noteCache.updateNote(noteEntity)

    override fun deleteNote(noteEntity: NoteEntity): Completable = noteCache.deleteNote(noteEntity)

    override fun deleteMultipleNotes(notes: List<NoteEntity>): Completable = noteCache.deleteMultipleNotes(notes)

}