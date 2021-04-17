package com.cleannote.data.test.container.stub

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteRemoteDataStore
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Single

class NoteRemoteDataStoreStubber(
    private val remoteDataStore: NoteRemoteDataStore
) : Stubber<NoteRemoteDataStoreStubber>() {

    fun searchNotes(param: QueryEntity, stub: List<NoteEntity>) {
        whenever(remoteDataStore.searchNotes(param)).thenReturn(Single.just(stub))
    }

    fun insertNote(param: NoteEntity, stub: Completable) {
        whenever(remoteDataStore.insertRemoteNewNote(param)).thenReturn(stub)
    }

    fun insertThrowable(param: NoteEntity, stub: Throwable) {
        whenever(remoteDataStore.insertRemoteNewNote(param)).thenReturn(Completable.error(stub))
    }
}
