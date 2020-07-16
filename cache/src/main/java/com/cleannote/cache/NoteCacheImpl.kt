package com.cleannote.cache

import android.content.SharedPreferences
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
                                        private val sharedPreferences: SharedPreferences):
    NoteCache {


    companion object{
        private const val SHOULD_PAGE_UPDATE_TIME = (60 * 3 * 1000).toLong()
        private const val PREF_KEY_LAST_CACHE = "last_cache_page_"
    }

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
            queryEntity.like)
        ).map { cachedNotes ->
            cachedNotes.map { entityMapper.mapFromCached(it) }
        }
    }

    override fun saveNotes(notes: List<NoteEntity>): Completable = Completable.defer {
        noteDao.saveNotes(notes.map { entityMapper.mapToCached(it) })
        Completable.complete()
    }

    override fun isCached(page: Int): Single<Boolean> = Single.defer {
        val currentTime = System.currentTimeMillis()
        val isCache = currentTime - getLastCacheTime(page) < SHOULD_PAGE_UPDATE_TIME
        Single.just(isCache)
    }

    override fun setLastCacheTime(lastCache: Long, page: Int) {
        sharedPreferences.edit().putLong(PREF_KEY_LAST_CACHE+page, lastCache).apply()
    }

    fun getLastCacheTime(page: Int): Long = sharedPreferences.getLong(PREF_KEY_LAST_CACHE+page,0)
}