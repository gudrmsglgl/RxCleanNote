package com.cleannote.data.source

import com.cleannote.data.repository.NoteRemote
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.cleannote.data.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteRemoteDataStoreTest {

    private lateinit var noteRemote: NoteRemote
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore
    private val noteEntity = NoteFactory.createNoteEntity("#1", "title#1", "body#1")
    private val noteEntities = NoteFactory.createNoteEntityList(0,5)

    @BeforeEach
    fun setUp(){
        noteRemote = mock{
            on { getNumNotes() }.doReturn(Flowable.just(NoteFactory.createNoteEntityList(0,10)))
            on { insertRemoteNewNote(noteEntity) } doReturn Completable.complete()
            on { login(UserFactory.USER_ID) } doReturn Flowable.just(UserFactory.userEntities())
        }
        noteRemoteDataStore = NoteRemoteDataStore(noteRemote)
    }

    @Test
    fun getNumNotesCompletes(){
        val testObserver = noteRemoteDataStore.getNumNotes().test()
        verify(noteRemote).getNumNotes()
        testObserver.assertComplete()
    }

    @Test
    fun insertRemoteNewNoteComplete(){
        val testObserver = noteRemoteDataStore.insertRemoteNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertCacheNewNoteReturnThrow(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            noteRemoteDataStore.insertCacheNewNote(noteEntity)
        }
    }

    @Test
    fun loginComplete(){
        val testObserver = noteRemoteDataStore.login(UserFactory.USER_ID).test()
        verify(noteRemote).login(any())
        testObserver.assertComplete()
    }

    @Test
    fun saveNotesReturnThrow(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            noteRemoteDataStore.saveNotes(noteEntities, QueryFactory.makeQueryEntity())
        }
    }

    @Test
    fun isCachedReturnThrow(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            noteRemoteDataStore.isCached(0)
        }
    }
}