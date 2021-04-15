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

    fun pageIsCache(param: Int, stub: Boolean){
        whenever(cacheDataStore.isCached(param)).thenReturn(Single.just(stub))
    }

    fun saveNotes(remoteNotes: List<NoteEntity>, queryEntity: QueryEntity, stub: Completable){
        whenever(cacheDataStore.saveNotes(remoteNotes, queryEntity)).thenReturn(stub)
    }

    fun currentPageNoteSize(param: QueryEntity, stub: Int){
        whenever(cacheDataStore.currentPageNoteSize(param)).thenReturn(Single.just(stub))
    }

    fun searchNotes(param: QueryEntity, stub: List<NoteEntity>){
        whenever(cacheDataStore.searchNotes(param)).thenReturn(Single.just(stub))
    }

    fun insertNote(param: NoteEntity, stub:Long){
        whenever(cacheDataStore.insertCacheNewNote(param)).thenReturn(Single.just(stub))
    }

    fun insertThrowable(param: NoteEntity, stub: Throwable){
        whenever(cacheDataStore.insertCacheNewNote(param)).thenReturn(Single.error(stub))
    }

    fun updateNote(param: NoteEntity, stub: Completable){
        whenever(cacheDataStore.updateNote(param)).thenReturn(stub)
    }

    fun deleteNote(param: NoteEntity, stub: Completable){
        whenever(cacheDataStore.deleteNote(param)).thenReturn(stub)
    }

    fun deleteMultipleNotes(param: List<NoteEntity>, stub: Completable){
        whenever(cacheDataStore.deleteMultipleNotes(param)).thenReturn(stub)
    }

    fun nextPageExist(param: QueryEntity, stub: Boolean) {
        whenever(cacheDataStore.nextPageExist(param)).thenReturn(Single.just(stub))
    }
}