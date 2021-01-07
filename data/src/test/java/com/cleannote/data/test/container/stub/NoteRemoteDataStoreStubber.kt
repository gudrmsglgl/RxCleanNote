package com.cleannote.data.test.container.stub

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteRemoteDataStore
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Single

class NoteRemoteDataStoreStubber(
    private val remoteDataStore: NoteRemoteDataStore
): Stubber<NoteRemoteDataStoreStubber>() {

    fun stubSearchNotes(param: QueryEntity, stub: List<NoteEntity>){
        whenever(remoteDataStore.searchNotes(param)).thenReturn(Single.just(stub))
    }

    fun stubSearchNotesThrowable(param: QueryEntity, stub: Throwable){
        whenever(remoteDataStore.searchNotes(param)).thenReturn(Single.error(stub))
    }

    fun stubInsertNote(param: NoteEntity, stub: Completable){
        whenever(remoteDataStore.insertRemoteNewNote(param)).thenReturn(stub)
    }

    fun stubInsertThrowable(param: NoteEntity, stub: Throwable){
        whenever(remoteDataStore.insertRemoteNewNote(param)).thenReturn(Completable.error(stub))
    }

    /*infix fun NoteRemoteDataStore.stubLogin(stub: Pair<String, List<UserEntity>>){
        whenever(this.login(stub.first)).thenReturn(Flowable.just(stub.second))
    }

    infix fun NoteRemoteDataStore.stubSearchNotes(stubEntities: Pair<QueryEntity, List<NoteEntity>>){
        whenever(this.searchNotes(stubEntities.first)).thenReturn(Single.just(stubEntities.second))
    }*/
}