package com.cleannote.data

import com.cleannote.data.extensions.*
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function3
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
            zip_IsCache_SearchNotes_CurPageCacheNoteSize(isCached, queryEntity)
        }
        .flatMap {
            when {
                isNotEmptyRemoteNotesGreaterThanOrEqCacheNotes(it) -> {
                    val remoteNotes = it.second
                    saveRemoteNotesThenReturnRemote(remoteNotes, queryEntity)
                }
                isNotEmptyRemoteNotesLessThanCachedNotes(it) -> {
                    val remoteNotes = it.second
                    saveRemoteNotesAndThenCacheSearchNotes(remoteNotes, queryEntity)
                }
                isEmptyRemoteNotes(it) -> {
                    returnSearchNoteOnCache(queryEntity)
                }
                else -> {
                    val cacheNotes = it.second
                    returnCachedNotes(cacheNotes.transNoteList())
                }
            }
        }

    private fun zip_IsCache_SearchNotes_CurPageCacheNoteSize(
        isCached: Boolean,
        queryEntity: QueryEntity
    ) = Single.zip(
        Single.just(isCached),
        searchNotesOnDataStore(
            dataStore = factory.retrieveDataStore(isCached),
            queryEntity = queryEntity
        ),
        (factory.retrieveCacheDataStore() as NoteCacheDataStore).currentPageNoteSize(queryEntity),
        Function3<Boolean, List<NoteEntity>, Int, Triple<Boolean, List<NoteEntity>, Int>>{ s1, s2, s3 ->
            Triple(s1, s2, s3)
        }
    )

    private fun isNotEmptyRemoteNotesGreaterThanOrEqCacheNotes(
        pendingData: Triple<Boolean, List<NoteEntity> , Int>
    ): Boolean{
        val isCache = pendingData.first
        val remoteData = pendingData.second
        val currentCacheNoteSize = pendingData.third
        val isRemoteNoteEqualGreaterThanCacheNotes = remoteData.size >= currentCacheNoteSize
        return !isCache && remoteData.isNotEmpty() && isRemoteNoteEqualGreaterThanCacheNotes
    }

    private fun isNotEmptyRemoteNotesLessThanCachedNotes(
        pendingData: Triple<Boolean, List<NoteEntity> , Int>
    ): Boolean{
        val isCache = pendingData.first
        val remoteData = pendingData.second
        val currentCacheNoteSize = pendingData.third
        val isRemoteNoteLessThanCacheNotes = remoteData.size < currentCacheNoteSize
        return !isCache && remoteData.isNotEmpty() && isRemoteNoteLessThanCacheNotes
    }
    
    private fun isEmptyRemoteNotes(
        pendingData: Triple<Boolean, List<NoteEntity> , Int>
    ): Boolean{
        val isCache = pendingData.first
        val remoteData = pendingData.second
        return !isCache && remoteData.isEmpty()
    }

    private fun searchNotesOnDataStore(
        dataStore: NoteDataStore,
        queryEntity: QueryEntity
    ): Single<List<NoteEntity>>{
        return dataStore.searchNotes(queryEntity)
    }

    private fun keywordSearchNote(
        queryEntity: QueryEntity
    ) = factory
        .retrieveRemoteDataStore()
        .searchNotes(queryEntity)
        .flatMap {
            if (it.isNotEmpty())
                saveRemoteNotesThenReturnRemote(it, queryEntity)
            else
                returnSearchNoteOnCache(queryEntity)
        }


    private fun saveRemoteNotesThenReturnRemote(
        remoteNotes: List<NoteEntity>,
        queryEntity: QueryEntity
    ) = saveRemoteNotesToCache(remoteNotes, queryEntity)
        .toSingle {
            remoteNotes.transNoteList()
        }

    private fun saveRemoteNotesAndThenCacheSearchNotes(remoteNotes: List<NoteEntity>,
                                                       queryEntity: QueryEntity) = factory
        .retrieveCacheDataStore()
        .saveNotes(remoteNotes, queryEntity)
        .andThen(
            Single.defer {
                returnSearchNoteOnCache(queryEntity)
            }
        )

    private fun returnSearchNoteOnCache(queryEntity: QueryEntity) = factory
        .retrieveCacheDataStore()
        .searchNotes(queryEntity)
        .map {
            it.transNoteList()
        }

    private fun returnCachedNotes(param: List<Note>) = Single.just(param)

    private fun saveRemoteNotesToCache(remoteEntities: List<NoteEntity>, queryEntity: QueryEntity): Completable {
        return factory.retrieveCacheDataStore().saveNotes(remoteEntities, queryEntity)
    }

}