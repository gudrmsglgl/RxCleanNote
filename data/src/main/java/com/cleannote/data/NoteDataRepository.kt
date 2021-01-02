package com.cleannote.data

import com.cleannote.data.extensions.*
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteDataStore
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

    override fun searchNotes(query: Query): Single<List<Note>> =
        if (query.like == null || query.like == "")
            defaultSearchNote(query.transQueryEntity())
        else
            keywordSearchNote(query.transQueryEntity())

    override fun updateNote(note: Note): Completable = factory
        .retrieveCacheDataStore()
        .updateNote(note.transNoteEntity())

    override fun deleteNote(note: Note): Completable = factory
        .retrieveCacheDataStore()
        .deleteNote(note.transNoteEntity())

    override fun deleteMultipleNotes(notes: List<Note>): Completable  = factory
        .retrieveCacheDataStore()
        .deleteMultipleNotes(notes.transNoteEntityList())

    private fun defaultSearchNote(
        queryEntity: QueryEntity
    ) = factory
        .retrieveCacheDataStore()
        .isCached(queryEntity.page)
        .flatMap { isCached ->
            searchNotesOnDataStore(
                dataStore = factory.retrieveDataStore(isCached),
                queryEntity = queryEntity
            )
            .zipWith(Single.just(isCached))
        }
        .flatMap {
            when {
                isRemoteLoadedNotes(it) -> {
                    val remoteNotes = it.first
                    returnRemoteNotes(queryEntity, remoteNotes)
                }
                isRemoteLoadedNotesEmpty(it) -> {
                    returnSearchNoteOnCache(queryEntity)
                }
                else -> {
                    val cacheNotes = it.first
                    Single.just(cacheNotes.transNoteList())
                }
            }
        }


    private fun searchNotesOnDataStore(
        dataStore: NoteDataStore,
        queryEntity: QueryEntity
    ): Single<List<NoteEntity>>{
        return dataStore.searchNotes(queryEntity)
    }

    private fun isRemoteLoadedNotes(pendingData: Pair<List<NoteEntity>, Boolean>): Boolean{
        return pendingData.first.isNotEmpty() && !pendingData.second
    }

    private fun isRemoteLoadedNotesEmpty(pendingData: Pair<List<NoteEntity>, Boolean>): Boolean{
        return pendingData.first.isEmpty() && !pendingData.second
    }

    private fun keywordSearchNote(
        queryEntity: QueryEntity
    ) = factory
        .retrieveRemoteDataStore()
        .searchNotes(queryEntity)
        .flatMap {
            if (it.isNotEmpty())
                returnRemoteNotes(queryEntity, it)
            else
                returnSearchNoteOnCache(queryEntity)
        }


    private fun returnRemoteNotes(
        queryEntity: QueryEntity,
        remoteNotes: List<NoteEntity>
    ) = saveRemoteNotesToCache(
            remoteNotes,
            queryEntity
        )
        .toSingle {
            remoteNotes.transNoteList()
        }

    private fun returnSearchNoteOnCache(queryEntity: QueryEntity) = factory
        .retrieveCacheDataStore()
        .searchNotes(queryEntity)
        .map {
            it.transNoteList()
        }

    private fun saveRemoteNotesToCache(remoteEntities: List<NoteEntity>, queryEntity: QueryEntity): Completable {
        return factory.retrieveCacheDataStore().saveNotes(remoteEntities, queryEntity)
    }

}