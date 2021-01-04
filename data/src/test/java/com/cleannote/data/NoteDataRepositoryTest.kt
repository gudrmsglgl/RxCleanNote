package com.cleannote.data

import com.cleannote.data.extensions.*
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.stub.DataStoreStubberContainer
import com.cleannote.data.test.verify.VerifierContainer
import com.cleannote.domain.model.Note
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteDataRepositoryTest: BaseCacheDataTest() {

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore
    private lateinit var noteDataStoreFactory: NoteDataStoreFactory

    private lateinit var stubContainer: DataStoreStubberContainer
    private lateinit var verifyContainer: VerifierContainer

    @BeforeEach
    fun setUp(){
        initMockDataStore()
        initMockStubDataStoreFactory()
        initContainer()
        noteDataRepository = NoteDataRepository(noteDataStoreFactory)
    }

    @Test
    fun whenInsertNewNoteThenCallCacheRemoteDataStore(){
        val insertNote: Note = NoteFactory.createNote(title = "title#1")
        val insertNoteEntity = insertNote.transNoteEntity()
        val insertSuccess = 1L

        stubContainer {
            remoteDataStore {
                stubInsertNote(param = insertNoteEntity, stub = Completable.complete())
            }
            cacheDataStore {
                stubInsertNote(param = insertNoteEntity, stub = insertSuccess)
            }
        }

        whenDataRepositoryInsertNote(insertNote)
            .test()
            .assertComplete()

        verifyContainer{
            inOrder(remoteDataStore, cacheDataStore) {

                verify(cacheDataStore)
                    .insertCacheNewNote(insertNoteEntity)

                verify(remoteDataStore)
                    .insertRemoteNewNote(insertNoteEntity)

            }
        }
    }

    /*@Test
    fun insertNewNoteReturnLongValue(){
        val insertNote: Note = NoteFactory.createNote(title = "title#1")
        val insertNoteEntity = insertNote.transNoteEntity()
        val insertSuccess = 1L

        noteCacheDataStore stubInsertNote (insertNoteEntity to insertSuccess)
        noteRemoteDataStore stubInsertNote (insertNoteEntity to Completable.complete())


        whenDataRepositoryInsertNote(insertNote)
            .test()
            .assertValue(insertSuccess)
    }

    @Test
    fun whenInsertNewNoteReturnFail(){
        val failNote = NoteFactory.createNote(title = "failNote")
        val failNoteEntity = failNote.transNoteEntity()
        val insertFail = -1L

        noteCacheDataStore stubInsertNote (failNoteEntity to insertFail)

        whenDataRepositoryInsertNote(failNote)
            .test()
            .assertValue(insertFail)
    }

    @Test
    fun whenInsertNewNoteFailThenNotCallRemote(){
        val failNote = NoteFactory.createNote(title = "failNote")
        val failNoteEntity = failNote.transNoteEntity()
        val insertFail = -1L

        noteCacheDataStore stubInsertNote (failNoteEntity to insertFail)

        whenDataRepositoryInsertNote(failNote)
        noteRemoteDataStore.verifyInsertNote(failNoteEntity, never())
    }

    @Test
    fun whenLoginThenComplete(){
        val userID = UserFactory.USER_ID
        val userEntities = UserFactory.userEntities()

        noteRemoteDataStore stubLogin (userID to userEntities)

        whenDataRepositoryLogin(userID)
            .test()
            .assertComplete()
    }

    @Test
    fun whenLoginReturnUsers(){
        val userID = UserFactory.USER_ID
        val userList = UserFactory.users()
        val userEntities = userList.transUserEntityList()

        noteRemoteDataStore stubLogin (userID to userEntities)

        whenDataRepositoryLogin(userID)
            .test()
            .assertValue(userList)
    }

    @Test
    fun whenSearchNotesThenComplete(){
        val cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)

        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = defaultQuery.transQueryEntity()

        noteCacheDataStore stubPageIsCache (defaultQueryEntity.page to false)
        noteRemoteDataStore stubSearchNotes (defaultQueryEntity to cacheNoteEntities)
        noteCacheDataStore stubSaveNotes (Triple(cacheNoteEntities, defaultQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (defaultQueryEntity to cacheNoteEntities)

        val testObserver = whenDataRepositorySearchNotes(defaultQuery).test()
        testObserver.assertComplete()
    }

    @Test
    fun whenSearchNotesThenReturnNotes(){
        val remoteNoteEntities = NoteFactory.createNoteEntityList(0, 10)
        val cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)
        val resultNote: List<Note> = cacheNoteEntities.transNoteList()

        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = defaultQuery.transQueryEntity()

        noteCacheDataStore stubPageIsCache (defaultQueryEntity.page to false)
        noteRemoteDataStore stubSearchNotes (defaultQueryEntity to remoteNoteEntities)
        noteCacheDataStore stubSaveNotes (Triple(remoteNoteEntities, defaultQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (defaultQueryEntity to cacheNoteEntities)

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(resultNote)
    }

    @Test
    fun whenRemoteDataIsNotEmptyThenSearchNotesSaveCacheRemoteData(){
        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = defaultQuery.transQueryEntity()

        val remoteNoteEntities = NoteFactory.createNoteEntityList(0, 10)
        val cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)

        noteCacheDataStore stubPageIsCache (defaultQueryEntity.page to false)
        noteRemoteDataStore stubSearchNotes (defaultQueryEntity to remoteNoteEntities)
        noteCacheDataStore stubSaveNotes (Triple(remoteNoteEntities, defaultQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (defaultQueryEntity to cacheNoteEntities)

        whenDataRepositorySearchNotes(defaultQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory) {

            with(noteCacheDataStore){
                verifyIsCached(defaultQueryEntity.page)
                assertIsCached(defaultQueryEntity.page , false)
            }

            noteDataStoreFactory.verifyRetrieveDataStore(false)

            noteRemoteDataStore.verifySearchNote(defaultQueryEntity)

            with(noteCacheDataStore) {
                verifySaveNote(remoteNoteEntities, defaultQueryEntity)
                verifySearchNote(defaultQueryEntity)
            }
        }
    }

    @Test
    fun whenNextSearchNotesIsCacheThenNotCallRemoteOnlyCacheData(){
        val nextPageQuery = QueryFactory.makeQuery(page = 2)
        val nextPageQueryEntity = nextPageQuery.transQueryEntity()

        val nextCachedNoteEntities = NoteFactory.createNoteEntityList(10,20)

        noteCacheDataStore stubPageIsCache (nextPageQueryEntity.page to true)
        noteCacheDataStore stubSearchNotes (nextPageQueryEntity to nextCachedNoteEntities)

        whenDataRepositorySearchNotes(nextPageQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory){

            with (noteCacheDataStore){
                verifyIsCached(nextPageQueryEntity.page)
                assertIsCached(nextPageQueryEntity.page, true)
            }

            noteDataStoreFactory.verifyRetrieveDataStore(true)

            noteRemoteDataStore.verifySearchNote(nextPageQueryEntity, never())

            with (noteCacheDataStore) {
                verifySaveNote(nextCachedNoteEntities, nextPageQueryEntity, never())
                verifySearchNote(nextPageQueryEntity)
            }
        }
    }

    @Test
    fun whenSearchNotesNextPageEmptyDataThenDontSaveCache(){
        val nextPageQuery = QueryFactory.makeQuery(page = 3)
        val nextPageQueryEntity = nextPageQuery.transQueryEntity()

        val nextRemoteNoteEntities = emptyList<NoteEntity>()
        val nextCachedNoteEntities = emptyList<NoteEntity>()

        noteCacheDataStore stubPageIsCache (nextPageQueryEntity.page to false)
        noteRemoteDataStore stubSearchNotes (nextPageQueryEntity to nextRemoteNoteEntities)
        noteCacheDataStore stubSearchNotes (nextPageQueryEntity to nextCachedNoteEntities)

        whenDataRepositorySearchNotes(nextPageQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory){

            with (noteCacheDataStore) {
                verifyIsCached(nextPageQueryEntity.page)
                assertIsCached(nextPageQueryEntity.page , false)
            }

            noteDataStoreFactory.verifyRetrieveDataStore(false)

            noteRemoteDataStore.verifySearchNote(nextPageQueryEntity)

            with (noteCacheDataStore) {
                verifySaveNote(nextRemoteNoteEntities, nextPageQueryEntity, never())
                verifySearchNote(nextPageQueryEntity)
            }
        }
    }

    @Test
    fun whenSearchKeywordNoteThenComplete(){
        val searchQuery = QueryFactory.makeQuery("#1")
        val searchQueryEntity = searchQuery.transQueryEntity()

        val searchedRemoteNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchedNote = searchedCacheNotes.transNoteList()


        noteRemoteDataStore stubSearchNotes (searchQueryEntity to searchedRemoteNotes)
        noteCacheDataStore stubSaveNotes (Triple(searchedRemoteNotes, searchQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (searchQueryEntity to searchedCacheNotes)

        verify(noteCacheDataStore, never()).isCached(any())

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(searchedNote)
            .assertComplete()
    }

    @Test
    fun whenSearchKeywordNoteRemoteNotEmptyThenSaveCache(){
        val searchQuery = QueryFactory.makeQuery("#1")
        val searchQueryEntity = searchQuery.transQueryEntity()

        val searchedRemoteNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))


        noteRemoteDataStore stubSearchNotes (searchQueryEntity to searchedRemoteNotes)
        noteCacheDataStore stubSaveNotes (Triple(searchedRemoteNotes, searchQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (searchQueryEntity to searchedCacheNotes)

        whenDataRepositorySearchNotes(searchQuery).test()

        noteRemoteDataStore.verifySearchNote(searchQueryEntity)
        with (noteCacheDataStore) {
            verifySaveNote(searchedRemoteNotes, searchQueryEntity)
            verifySearchNote(searchQueryEntity)
        }
    }

    @Test
    fun whenSearchKeywordNoteRemoteNoDataCacheExistData_ThenNotSaveCacheReturnCacheData(){
        val searchQuery = QueryFactory.makeQuery("#1")
        val searchQueryEntity = searchQuery.transQueryEntity()

        val searchedRemoteNotes = emptyList<NoteEntity>()
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))

        noteRemoteDataStore stubSearchNotes (searchQueryEntity to searchedRemoteNotes)
        noteCacheDataStore stubSearchNotes (searchQueryEntity to searchedCacheNotes)

        whenDataRepositorySearchNotes(searchQuery).test()

        noteRemoteDataStore.verifySearchNote(searchQueryEntity)
        with(noteCacheDataStore){
            verifySaveNote(searchedRemoteNotes, searchQueryEntity, never())
            verifySearchNote(searchQueryEntity)
        }
    }

    @Test
    fun whenUpdateNoteThenComplete(){
        val updateNote = NoteFactory.createNote(title = "updateNote")
        val updateNoteEntity = updateNote.transNoteEntity()
        noteCacheDataStore stubUpdateNote (updateNoteEntity to Completable.complete())

        whenUpdateNote(updateNote)
            .test()
            .assertComplete()
    }

    @Test
    fun whenUpdateNoteThenNoValue(){
        val updateNote = NoteFactory.createNote(title = "updateNote")
        val updateNoteEntity = updateNote.transNoteEntity()
        noteCacheDataStore stubUpdateNote (updateNoteEntity to Completable.complete())

        whenUpdateNote(updateNote)
            .test()
            .assertComplete()
            .assertNoValues()

        verify(noteDataStoreFactory).retrieveCacheDataStore()
        verify(noteDataStoreFactory, never()).retrieveRemoteDataStore()
    }

    @Test
    fun whenDeleteNoteThenComplete(){
        val deleteNote = NoteFactory.createNote(title = "deleteNote")
        val deleteNoteEntity = deleteNote.transNoteEntity()
        noteCacheDataStore stubDeleteNote (deleteNoteEntity to Completable.complete())

        whenDataRepositoryDeleteNote(deleteNote)
            .test()
            .assertComplete()
            .assertNoValues()

        noteCacheDataStore.verifyDeleteNote(deleteNoteEntity)
    }

    @Test
    fun whenDeleteMultipleNotesThenCallOnlyCache(){
        val deleteNotes = NoteFactory.createNoteList(0,3)
        val deleteNoteEntities = deleteNotes.transNoteEntityList()

        noteCacheDataStore stubDeleteMultiNotes (deleteNoteEntities to Completable.complete())

        whenDataRepositoryDeleteMultiNotes(deleteNotes)
            .test()
            .assertComplete()
            .assertNoValues()

        noteRemoteDataStore.verifyDeleteMultiNotes(deleteNoteEntities, never())
        noteCacheDataStore.verifyDeleteMultiNotes(deleteNoteEntities, times(1))
    }*/

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