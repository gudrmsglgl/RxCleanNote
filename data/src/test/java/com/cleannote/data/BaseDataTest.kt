package com.cleannote.data

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.verification.VerificationMode

abstract class BaseDataTest {

    lateinit var noteDataRepository: NoteDataRepository

    infix fun NoteCacheDataStore.stubInsertNote(stub: Pair<NoteEntity, Long>){
        whenever(this.insertCacheNewNote(stub.first)).thenReturn(Single.just(stub.second))
    }

    infix fun NoteDataStore.stubInsertNote(stub: Pair<NoteEntity, Completable>){
        whenever(this.insertRemoteNewNote(stub.first)).thenReturn(stub.second)
    }

    infix fun NoteRemoteDataStore.stubLogin(stub: Pair<String, List<UserEntity>>){
        whenever(this.login(stub.first)).thenReturn(Flowable.just(stub.second))
    }

    infix fun NoteCacheDataStore.stubSaveNotes(stub: Triple<List<NoteEntity>, QueryEntity, Completable>){
        whenever(this.saveNotes(stub.first, stub.second)).thenReturn(stub.third)
    }

    infix fun NoteCacheDataStore.stubPageIsCache(stub: Pair<Int, Boolean>){
        whenever(this.isCached(stub.first)).thenReturn(Single.just(stub.second))
    }

    infix fun NoteRemoteDataStore.stubSearchNotes(stubEntities: Pair<QueryEntity, List<NoteEntity>>){
        whenever(this.searchNotes(stubEntities.first)).thenReturn(Single.just(stubEntities.second))
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
    }

    fun whenDataRepositoryInsertNote(note: Note) = noteDataRepository.insertNewNote(note)

    fun NoteDataStore.verifyInsertNote(noteEntity: NoteEntity, verifyMode: VerificationMode? = null){
        if (this is NoteCacheDataStore)
            verify(this, verifyMode ?: times(1)).insertCacheNewNote(noteEntity)
        else
            verify(this, verifyMode ?: times(1)).insertRemoteNewNote(noteEntity)
    }

    fun whenDataRepositoryLogin(userId: String) = noteDataRepository.login(userId)

    fun whenDataRepositorySearchNotes(query: Query) = noteDataRepository.searchNotes(query)

    fun whenUpdateNote(note: Note) = noteDataRepository.updateNote(note)

    fun whenDataRepositoryDeleteNote(note: Note) = noteDataRepository.deleteNote(note)

    fun whenDataRepositoryDeleteMultiNotes(notes: List<Note>) = noteDataRepository.deleteMultipleNotes(notes)

    fun NoteCacheDataStore.verifyIsCached(page: Int){
        verify(this).isCached(page)
    }

    fun NoteCacheDataStore.assertIsCached(page:Int, expectBool: Boolean) = assertThat(
        this.isCached(page).blockingGet(),
        `is`(expectBool)
    )

    fun NoteDataStoreFactory.verifyRetrieveDataStore(expectBool: Boolean){
        verify(this).retrieveDataStore(expectBool)
    }

    fun NoteRemoteDataStore.verifySearchNote(queryEntity: QueryEntity, verifyMode: VerificationMode?=null){
        verify(this, verifyMode ?: times(1)).searchNotes(queryEntity)
    }

    fun NoteCacheDataStore.verifySaveNote(
        remoteNotes: List<NoteEntity>,
        queryEntity: QueryEntity,
        verifyMode: VerificationMode? = null){
            verify(this, verifyMode ?: times(1)).saveNotes(remoteNotes, queryEntity)
        }

    fun NoteCacheDataStore.verifySearchNote(queryEntity: QueryEntity, verifyMode: VerificationMode?=null){
        verify(this, verifyMode ?: times(1)).searchNotes(queryEntity)
    }

    fun NoteCacheDataStore.verifyDeleteNote(noteEntity: NoteEntity, verifyMode: VerificationMode? = null){
        verify(this,verifyMode ?: times(1)).deleteNote(noteEntity)
    }

    fun NoteDataStore.verifyDeleteMultiNotes(noteEntities: List<NoteEntity>, verifyMode: VerificationMode? = null){
        verify(this, verifyMode ?: times(1)).deleteMultipleNotes(noteEntities)
    }
}