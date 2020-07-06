package com.cleannote.cache

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.dao.searchNoteBySorted
import com.cleannote.cache.mapper.NoteEntityMapper
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteCache
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class NoteCacheImpl @Inject constructor(val noteDao: CachedNoteDao,
                                        private val entityMapper: NoteEntityMapper,
                                        private val preferencesHelper: PreferencesHelper):
    NoteCache {

    private val SHOULD_PAGE_UPDATE_TIME = (60 * 3 * 1000).toLong()

    override fun getNumNotes(): Flowable<List<NoteEntity>> = Flowable.defer {
        Flowable.just(noteDao.getNumNotes())
    }.map { cachedNotes ->
        cachedNotes.map {
            entityMapper.mapFromCached(it)
        }
    }

    override fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long> =
        Single.defer{
            Single.just(noteDao.insertNote(entityMapper.mapToCached(noteEntity)))
        }

    override fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>> = Flowable.defer {
        Flowable.just(noteDao.searchNoteBySorted(
            queryEntity.page,
            queryEntity.limit,
            queryEntity.order,
            queryEntity.sort,
            queryEntity.like)
        ).map { cachedNotes ->
            cachedNotes.map { entityMapper.mapFromCached(it) }
        }
    }

    override fun saveNotes(notes: List<NoteEntity>, page: Int): Completable = Completable.defer {
        noteDao.saveNotes(notes.map { entityMapper.mapToCached(it) })
        Completable.complete()
    }

    override fun isCached(page: Int): Single<Boolean> = Single.defer {
        val currentTime = System.currentTimeMillis()
        val isUpdateTime = currentTime - getLastCacheTime(page) > SHOULD_PAGE_UPDATE_TIME
        Single.just(isUpdateTime)
    }

    override fun setLastCacheTime(lastCache: Long, page: Int) {
        preferencesHelper.setLastCacheTime(lastCache, page)
    }

    private fun getLastCacheTime(page: Int) = preferencesHelper.getLastCacheTime(page)
}