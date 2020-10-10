package com.cleannote.data

import com.cleannote.data.extensions.*
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import java.lang.Exception
import javax.inject.Inject

class NoteDataRepository
@Inject
constructor(
    private val factory: NoteDataStoreFactory
): NoteRepository{

    override fun insertNewNote(note: Note): Single<Long> = factory.retrieveCacheDataStore()
        .insertCacheNewNote(note.transNoteEntity())
        .map {
            if (it > 0) {
                factory.retrieveRemoteDataStore().insertRemoteNewNote(note.transNoteEntity())
                it
            } else {
                throw Exception()
            }
        }
        .onErrorReturn { -1L }

    override fun login(userId: String): Flowable<List<User>> = factory
        .retrieveRemoteDataStore()
        .login(userId)
        .map {
            it.transUserList()
        }

    override fun searchNotes(query: Query): Flowable<List<Note>> =
        if (query.like == null || query.like == "")
            defaultSearchNote(query.transQueryEntity())
        else
            keywordSearchNote(query.transQueryEntity())

    private fun defaultSearchNote(
        queryEntity: QueryEntity
    ) = factory
        .retrieveCacheDataStore()
        .isCached(queryEntity.page)
        .flatMapPublisher {
            factory.retrieveDataStore(it).searchNotes(queryEntity)
                .zipWith(Flowable.just(it))
        }
        .flatMapSingle {
            val isCached: Boolean = it.second
            val loadedNoteEntities: List<NoteEntity> = it.first
            if (!isCached && loadedNoteEntities.isNotEmpty())
                saveNotes(
                    remoteEntities = loadedNoteEntities,
                    queryEntity = queryEntity
                ).toSingle { it }
            else Single.just(it)
        }
        .flatMap {
            val isCached: Boolean = it.second
            val loadedNoteEntities: List<NoteEntity> = it.first
            if (isCached)
                returnCacheNoteEntities(loadedNoteEntities)
            else
                loadUpdatedCacheNoteEntities(queryEntity)
        }

    private fun keywordSearchNote(
        queryEntity: QueryEntity
    ) = factory
        .retrieveRemoteDataStore()
        .searchNotes(queryEntity)
        .flatMapSingle {
            if (it.isNotEmpty())
                saveNotes(it, queryEntity).toSingle { it }
            else Single.just(it)
        }
        .flatMap {
            loadUpdatedCacheNoteEntities(queryEntity)
        }

    override fun updateNote(note: Note): Completable = factory
        .retrieveCacheDataStore()
        .updateNote(note.transNoteEntity())

    override fun deleteNote(note: Note): Completable = factory
        .retrieveCacheDataStore()
        .deleteNote(note.transNoteEntity())

    override fun deleteMultipleNotes(notes: List<Note>): Completable  = factory
        .retrieveCacheDataStore()
        .deleteMultipleNotes(notes.transNoteEntityList())

    /*override fun searchNotes(query: Query): Flowable<List<Note>> {
        val queryEntity = queryMapper.mapToEntity(query)
        return factory.retrieveRemoteDataStore().searchNotes(queryEntity)
            .doOnNext { if (it.isNotEmpty()) saveNotes(it) }
            .mergeWith(factory.retrieveCacheDataStore().searchNotes(queryEntity))
            .flatMapIterable { it }
            .distinct()
            .map { noteMapper.mapFromEntity(it) }
            .toList()
            .toFlowable()

    }*/

    private fun loadUpdatedCacheNoteEntities(
        queryEntity: QueryEntity
    ): Flowable<List<Note>> = factory
        .retrieveCacheDataStore()
        .searchNotes(queryEntity)
        .map {
            it.transNoteList()
        }


    private fun returnCacheNoteEntities(cacheEntities: List<NoteEntity>): Flowable<List<Note>>{
        return Flowable.just(cacheEntities.transNoteList())
    }

    private fun saveNotes(remoteEntities: List<NoteEntity>, queryEntity: QueryEntity): Completable {
        return factory.retrieveCacheDataStore().saveNotes(remoteEntities, queryEntity)
    }

}