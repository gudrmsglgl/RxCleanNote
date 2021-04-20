package com.cleannote.data.source

import com.cleannote.data.repository.NoteRemote
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.cleannote.data.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteRemoteDataStoreTest {

    private lateinit var noteRemote: NoteRemote
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore

    private val noteEntity = NoteFactory.createNoteEntity("#1", "title#1", "body#1")
    private val noteEntities = NoteFactory.createNoteEntityList(0, 5)
    private val queryEntity = QueryFactory.makeQueryEntity(search = "")

    @BeforeEach
    fun setUp() {
        noteRemote = mock {
            on { insertRemoteNewNote(noteEntity) } doReturn Completable.complete()
            on { login(UserFactory.USER_ID) } doReturn Flowable.just(UserFactory.userEntities())
            on { searchNotes(queryEntity) } doReturn Single.just(noteEntities)
        }
        noteRemoteDataStore = NoteRemoteDataStore(noteRemote)
    }

    @Test
    fun searchNotesCallRemoteFunc() {
        noteRemoteDataStore.searchNotes(queryEntity).test()
        verify(noteRemote).searchNotes(queryEntity)
    }

    @Test
    fun searchNotesComplete() {
        noteRemoteDataStore.searchNotes(queryEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun searchNotesReturnNoteEntities() {
        noteRemoteDataStore.searchNotes(queryEntity)
            .test()
            .assertValue(noteEntities)
    }

    @Test
    fun insertRemoteNewNoteCallRemoteFunc() {
        noteRemoteDataStore.insertRemoteNewNote(noteEntity).test()
        verify(noteRemote).insertRemoteNewNote(noteEntity)
    }

    @Test
    fun insertRemoteNewNoteComplete() {
        noteRemoteDataStore.insertRemoteNewNote(noteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun insertRemoteNewNoteReturnNoValue() {
        noteRemoteDataStore.insertRemoteNewNote(noteEntity)
            .test()
            .assertNoValues()
    }

    @Test
    fun insertCacheNewNoteReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.insertCacheNewNote(noteEntity)
        }
    }

    /*@Test
    fun loginComplete(){
        val testObserver = noteRemoteDataStore.login(UserFactory.USER_ID).test()
        verify(noteRemote).login(any())
        testObserver.assertComplete()
    }*/

    @Test
    fun saveNotesReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.saveNotes(noteEntities, QueryFactory.makeQueryEntity())
        }
    }

    @Test
    fun isCachedReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.isCached(0)
        }
    }

    @Test
    fun updateNoteReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.updateNote(noteEntity)
        }
    }

    @Test
    fun deleteNoteReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.deleteNote(noteEntity)
        }
    }

    @Test
    fun deleteMultipleNotesReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteDataStore.deleteMultipleNotes(noteEntities)
        }
    }
}
