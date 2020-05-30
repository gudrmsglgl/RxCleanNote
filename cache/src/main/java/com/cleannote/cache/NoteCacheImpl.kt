package com.cleannote.cache

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.mapper.NoteEntityMapper
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.repository.NoteCache
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class NoteCacheImpl @Inject constructor(val noteDao: CachedNoteDao,
                                        private val entityMapper: NoteEntityMapper): NoteCache {

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
}