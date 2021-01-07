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

    fun stubUpdateNote(param: NoteEntity, stub: Completable){
        whenever(cacheDataStore.updateNote(param)).thenReturn(stub)
    }

    fun stubDeleteNote(param: NoteEntity, stub: Completable){
        whenever(cacheDataStore.deleteNote(param)).thenReturn(stub)
    }

    fun stubDeleteMultipleNotes(param: List<NoteEntity>, stub: Completable){
        whenever(cacheDataStore.deleteMultipleNotes(param)).thenReturn(stub)
    }
}