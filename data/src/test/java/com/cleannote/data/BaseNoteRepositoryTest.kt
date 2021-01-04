package com.cleannote.data

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.container.stub.DataStoreStubberContainer
import com.cleannote.data.test.container.verify.VerifierContainer
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.BeforeEach
/*
*  [DataStore Success Or Fail]
*    - 1 [""] ThenCallRemoteOrCache
*    - 2 [""] ThenVerifyOrderingRemoteOrCache
*    - 3 [""] ThenAssertComplete
*    - 4 [""] ThenAssertValue
* */
abstract class BaseNoteRepositoryTest {

    lateinit var noteDataRepository: NoteDataRepository

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore
    private lateinit var noteDataStoreFactory: NoteDataStoreFactory

    lateinit var stubContainer: DataStoreStubberContainer
    lateinit var verifyContainer: VerifierContainer

    @BeforeEach
    fun setUp(){
        initMockDataStore()
        initMockStubDataStoreFactory()
        initContainer()
        noteDataRepository = NoteDataRepository(noteDataStoreFactory)
    }

    fun whenDataRepositoryInsertNote(note: Note) = noteDataRepository.insertNewNote(note)

    fun whenDataRepositoryLogin(userId: String) = noteDataRepository.login(userId)

    fun whenDataRepositorySearchNotes(query: Query) = noteDataRepository.searchNotes(query)

    fun whenUpdateNote(note: Note) = noteDataRepository.updateNote(note)

    fun whenDataRepositoryDeleteNote(note: Note) = noteDataRepository.deleteNote(note)

    fun whenDataRepositoryDeleteMultiNotes(notes: List<Note>) = noteDataRepository.deleteMultipleNotes(notes)

    private fun initMockStubDataStoreFactory(){
        noteDataStoreFactory = mock{
            on { retrieveRemoteDataStore() } doReturn noteRemoteDataStore
            on { retrieveCacheDataStore() } doReturn noteCacheDataStore
            on { retrieveDataStore(true) } doReturn noteCacheDataStore
            on { retrieveDataStore(false) } doReturn noteRemoteDataStore
        }
    }

    private fun initMockDataStore(){
        noteCacheDataStore = mock()
        noteRemoteDataStore = mock()
    }

    private fun initContainer(){
        stubContainer = DataStoreStubberContainer(noteRemoteDataStore,noteCacheDataStore)
        verifyContainer = VerifierContainer(noteDataStoreFactory, noteRemoteDataStore, noteCacheDataStore)
    }

}