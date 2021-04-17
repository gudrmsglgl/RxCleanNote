package com.cleannote.data

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.container.stub.DataStoreStubberContainer
import com.cleannote.data.test.container.verify.DataStoreVerifyScope
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

    protected lateinit var noteDataRepository: NoteDataRepository

    private lateinit var cDataStore: NoteCacheDataStore
    private lateinit var rDataStore: NoteRemoteDataStore
    private lateinit var noteDataStoreFactory: NoteDataStoreFactory

    lateinit var stubContainer: DataStoreStubberContainer
    lateinit var dataStoreVerifyScope: DataStoreVerifyScope

    @BeforeEach
    fun setUp() {
        initMockDataStore()
        initMockStubDataStoreFactory()
        initContainer()
        noteDataRepository = NoteDataRepository(noteDataStoreFactory)
    }

    private fun initMockStubDataStoreFactory() {
        noteDataStoreFactory = mock {
            on { retrieveRemoteDataStore() } doReturn rDataStore
            on { retrieveCacheDataStore() } doReturn cDataStore
            on { retrieveDataStore(true) } doReturn cDataStore
            on { retrieveDataStore(false) } doReturn rDataStore
        }
    }

    private fun initMockDataStore() {
        cDataStore = mock()
        rDataStore = mock()
    }

    private fun initContainer() {
        stubContainer = DataStoreStubberContainer(rDataStore, cDataStore)
        dataStoreVerifyScope = DataStoreVerifyScope(noteDataStoreFactory, rDataStore, cDataStore)
    }
}
