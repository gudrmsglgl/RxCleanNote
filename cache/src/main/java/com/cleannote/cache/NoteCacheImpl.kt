package com.cleannote.cache

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.extensions.currentNoteSize
import com.cleannote.cache.extensions.divideCacheNote
import com.cleannote.cache.extensions.nextPageIsExist
import com.cleannote.cache.extensions.searchNoteBySorted
import com.cleannote.cache.extensions.transEntity
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteCache
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class NoteCacheImpl @Inject constructor(
    private val noteDao: CachedNoteDao,
    private val preferencesHelper: PreferencesHelper
) : NoteCache {

    private val shouldUpdateTime = (60 * 3 * 1000).toLong()

    override fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long> =
        Single.defer {
            Single.just(noteDao.insertNote(noteEntity.divideCacheNote()))
        }

    override fun searchNotes(queryEntity: QueryEntity): Single<List<NoteEntity>> = Single.defer {
        Single.just(
            noteDao.searchNoteBySorted(queryEntity)
        ).map { cachedNotes ->
            cachedNotes.map { it.transEntity() }
        }
    }

    override fun saveNotes(notes: List<NoteEntity>): Completable = Completable.defer {
        noteDao.saveNoteAndImages(notes)
        Completable.complete()
    }

    override fun isCached(page: Int): Single<Boolean> = Single.defer {
        val currentTime = System.currentTimeMillis()
        val isCache = currentTime - getLastCacheTime(page) < shouldUpdateTime
        Single.just(isCache)
    }

    override fun setLastCacheTime(lastCache: Long, page: Int) {
        preferencesHelper.setLastCacheTime(lastCache, page)
    }

    override fun updateNote(noteEntity: NoteEntity): Completable = Completable.defer {
        noteDao.updateNoteAndImages(noteEntity)
        Completable.complete()
    }

    override fun deleteNote(noteEntity: NoteEntity): Completable = Completable.defer {
        noteDao.deleteNote(
            noteEntity.divideCacheNote()
        )
        Completable.complete()
    }

    override fun deleteMultipleNotes(notes: List<NoteEntity>): Completable = Completable.defer {
        noteDao.deleteMultipleNotes(
            notes.map { it.divideCacheNote() }
        )
        Completable.complete()
    }

    override fun currentPageNoteSize(queryEntity: QueryEntity): Single<Int> = Single.defer {
        Single.just(
            noteDao.currentNoteSize(queryEntity)
        )
    }

    override fun nextPageExist(queryEntity: QueryEntity): Single<Boolean> = Single.defer {
        Single.just(
            noteDao.nextPageIsExist(queryEntity)
        )
    }

    private fun getLastCacheTime(page: Int) = preferencesHelper.getLastCacheTime(page)
}
