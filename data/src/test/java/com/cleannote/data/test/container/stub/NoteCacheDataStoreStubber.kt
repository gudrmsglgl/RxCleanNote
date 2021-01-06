package com.cleannote.data.test.container.stub

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteCacheDataStore
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Single

class NoteCacheDataStoreStubber(
    private val cacheDataStore: NoteCacheDataStore
): Stubber<NoteCacheDataStoreStubber>() {

    fun stubPageIsCache(param: Int, stub: Boolean){
        whenever(cacheDataStore.isCached(param)).thenReturn(Single.just(stub))
    }

    fun stubSaveNotes(remoteNotes: List<NoteEntity>, queryEntity: QueryEntity, stub: Completable){
        whenever(cacheDataStore.saveNotes(remoteNotes, queryEntity)).thenReturn(stub)
    }

    fun stubCurrentPageNoteSize(param: Int, stub: Int){
        whenever(cacheDataStore.currentPageNoteSize(param)).thenReturn(Single.just(stub))
    }

    fun stubSearchNotes(param: QueryEntity, stub: List<NoteEntity>){
        whenever(cacheDataStore.searchNotes(param)).thenReturn(Single.just(stub))
    }

    fun stubSearchNotesThrowable(param: QueryEntity, stub: Throwable){
        whenever(cacheDataStore.searchNotes(param)).thenReturn(Single.error(stub))
    }

    fun stubInsertNote(param: NoteEntity, stub:Long){
        whenever(cacheDataStore.insertCacheNewNote(param)).thenReturn(Single.just(stub))
    }

    fun stubInsertThrowable(param: NoteEntity, stub: Throwable){
        whenever(cacheDataStore.insertCacheNewNote(param)).thenReturn(Single.error(stub))
    }

    /*infix fun NoteCacheDataStore.stubSaveNotes(stub: Triple<List<NoteEntity>, QueryEntity, Completable>){
        whenever(this.saveNotes(stub.first, stub.second)).thenReturn(stub.third)
    }

    infix fun NoteCacheDataStore.stubPageIsCache(stub: Pair<Int, Boolean>){
        whenever(this.isCached(stub.first)).thenReturn(Single.just(stub.second))
    }

    infix fun NoteCacheDataStore.stubSearchNotes(stubEntities: Pair<QueryEntity, List<NoteEntity>>){
        whenever(this.searchNotes(stubEntities.first)).thenReturn(Single.just(stubEntities.second))
    }

    infix fun NoteDataStore.stubUpdateNote(stub: Pair<NoteEntity, Completable>){
        whenever(this.updateNote(stub.first)).thenReturn(stub.second)
    }

    infix fun NoteCacheDataStore.stubDeleteNote(stub: Pair<NoteEntity, Completable>){
        whenever(this.deleteNote(stub.first)).thenReturn(stub.second)
    }

    infix fun NoteCacheDataStore.stubDeleteMultiNotes(stub: Pair<List<NoteEntity>, Completable>) {
        whenever(this.deleteMultipleNotes(stub.first)).thenReturn(stub.second)
    }*/
}