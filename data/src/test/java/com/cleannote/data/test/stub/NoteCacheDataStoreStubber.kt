package com.cleannote.data.test.stub

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteRemoteDataStore
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.mockito.verification.VerificationMode

class NoteCacheDataStoreStubber(private val dataStore: NoteCacheDataStore): Stubber<NoteCacheDataStoreStubber>() {

    fun stubInsertNote(param: NoteEntity, stub:Long){
        whenever(dataStore.insertCacheNewNote(param)).thenReturn(Single.just(stub))
    }

    infix fun VerificationMode.verifyInsertNote(param: NoteEntity){
        verify(dataStore, this).insertCacheNewNote(param)
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