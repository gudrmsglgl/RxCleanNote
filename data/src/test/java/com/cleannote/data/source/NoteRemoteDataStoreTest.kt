package com.cleannote.data.source

import com.cleannote.data.repository.NoteRemote
import com.cleannote.data.test.factory.NoteFactory
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

    @BeforeEach
    fun setUp(){
        noteRemote = mock{
            on { getNumNotes() }.doReturn(Flowable.just(NoteFactory.createNoteEntityList(10)))
            on { insertRemoteNewNote(noteEntity) } doReturn Completable.complete()
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
}