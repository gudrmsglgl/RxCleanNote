package com.cleannote.data

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.cleannote.data.test.factory.UserFactory
import com.cleannote.domain.model.Note
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteDataRepositoryTest: BaseDataTest() {

    private lateinit var noteDataStoreFactory: NoteDataStoreFactory

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore

    @BeforeEach
    fun setUp(){
        queryMapper = mock()
        noteMapper = mock()
        userMapper = mock ()
        noteCacheDataStore = mock()
        noteRemoteDataStore = mock()
        noteDataStoreFactory = mock{
            on { retrieveRemoteDataStore() } doReturn noteRemoteDataStore
            on { retrieveCacheDataStore() } doReturn noteCacheDataStore
            on { retrieveDataStore(true) } doReturn noteCacheDataStore
            on { retrieveDataStore(false) } doReturn noteRemoteDataStore
        }
        noteDataRepository = NoteDataRepository(noteDataStoreFactory, noteMapper, userMapper, queryMapper)
    }

    @Test
    fun whenInsertNewNoteThenCallCacheRemoteDataStore(){
        val insertNote: Note = NoteFactory.createNote(title = "title#1")
        val insertNoteEntity = NoteFactory.createNoteEntity(title = "title#1")
        val insertSuccess = 1L

        insertNote stubTo insertNoteEntity
        noteCacheDataStore stubInsertNote (insertNoteEntity to insertSuccess)
        noteRemoteDataStore stubInsertNote (insertNoteEntity to Completable.complete())

        whenDataRepositoryInsertNote(insertNote)
            .test()
            .assertComplete()

        noteCacheDataStore.verifyInsertNote(insertNoteEntity)
        noteRemoteDataStore.verifyInsertNote(insertNoteEntity)
    }

    @Test
    fun insertNewNoteReturnLongValue(){
        val insertNote: Note = NoteFactory.createNote(title = "title#1")
        val insertNoteEntity = NoteFactory.createNoteEntity(title = "title#1")
        val insertSuccess = 1L

        insertNote stubTo insertNoteEntity
        noteCacheDataStore stubInsertNote (insertNoteEntity to insertSuccess)
        noteRemoteDataStore stubInsertNote (insertNoteEntity to Completable.complete())


        whenDataRepositoryInsertNote(insertNote)
            .test()
            .assertValue(insertSuccess)
    }

    @Test
    fun whenInsertNewNoteReturnFail(){
        val failNote = NoteFactory.createNote(title = "failNote")
        val failNoteEntity = NoteFactory.createNoteEntity(title = "failNote")
        val insertFail = -1L

        failNote stubTo failNoteEntity
        noteCacheDataStore stubInsertNote (failNoteEntity to insertFail)

        whenDataRepositoryInsertNote(failNote)
            .test()
            .assertValue(insertFail)
    }

    @Test
    fun whenInsertNewNoteFailThenNotCallRemote(){
        val failNote = NoteFactory.createNote(title = "failNote")
        val failNoteEntity = NoteFactory.createNoteEntity(title = "failNote")
        val insertFail = -1L

        failNote stubTo failNoteEntity
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
        val userEntities = UserFactory.userEntities()

        noteRemoteDataStore stubLogin (userID to userEntities)

        userList.forEachIndexed { index, user ->
            whenever(userMapper.mapFromEntity(userEntities[index])).thenReturn(user)
        }

        whenDataRepositoryLogin(userID)
            .test()
            .assertValue(userList)
    }

    @Test
    fun whenSearchNotesThenComplete(){
        val cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            cacheNoteEntities[index] stubTo note
        }

        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = QueryFactory.makeQueryEntity()
        defaultQuery stubTo defaultQueryEntity

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
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            cacheNoteEntities[index] stubTo note
        }

        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = QueryFactory.makeQueryEntity()
        defaultQuery stubTo defaultQueryEntity

        noteCacheDataStore stubPageIsCache (defaultQueryEntity.page to false)
        noteRemoteDataStore stubSearchNotes (defaultQueryEntity to remoteNoteEntities)
        noteCacheDataStore stubSaveNotes (Triple(remoteNoteEntities, defaultQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (defaultQueryEntity to cacheNoteEntities)

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(resultNote)
    }

    @Test
    fun whenSearchNotesSaveCacheFromRemoteData(){
        val defaultQuery = QueryFactory.makeQuery()
        val defaultQueryEntity = QueryFactory.makeQueryEntity()
        defaultQuery stubTo defaultQueryEntity

        val remoteNoteEntities = NoteFactory.createNoteEntityList(0, 10)
        val cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            cacheNoteEntities[index] stubTo note
        }

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
    fun whenSearchNotesThenNotCallRemoteOnlyCacheData(){
        val nextPageQuery = QueryFactory.makeQuery(page = 2)
        val nextPageQueryEntity = QueryFactory.makeQueryEntity(page = 2)
        nextPageQuery stubTo nextPageQueryEntity

        val nextCachedNoteEntities = NoteFactory.createNoteEntityList(10,20)
        val resultNote: List<Note> = NoteFactory.createNoteList(10,20)
        resultNote.forEachIndexed { index, note ->
            nextCachedNoteEntities[index] stubTo note
        }

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
    fun whenSearchNotesNextPageThenNoData(){
        val nextPageQuery = QueryFactory.makeQuery(page = 3)
        val nextPageQueryEntity = QueryFactory.makeQueryEntity(page = 3)
        nextPageQuery stubTo nextPageQueryEntity

        val nextRemoteNoteEntities = emptyList<NoteEntity>()
        val nextCachedNoteEntities = emptyList<NoteEntity>()

        val resultNote: List<Note> = emptyList()
        resultNote.forEachIndexed { index, note ->
            nextCachedNoteEntities[index] stubTo note
        }

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
        val searchQueryEntity = QueryFactory.makeQueryEntity("#1")
        searchQuery stubTo searchQueryEntity

        val searchedNote = listOf(NoteFactory.createNote(title = "testTitle#1"))
        val searchedRemoteNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        searchedNote.forEachIndexed { index, note ->
            searchedCacheNotes[index] stubTo note
        }

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
    fun whenSearchKeywordNoteThenSaveCacheLoadCache(){
        val searchQuery = QueryFactory.makeQuery("#1")
        val searchQueryEntity = QueryFactory.makeQueryEntity("#1")
        searchQuery stubTo searchQueryEntity

        val searchedNote = listOf(NoteFactory.createNote(title = "testTitle#1"))
        val searchedRemoteNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        searchedNote.forEachIndexed { index, note ->
            searchedCacheNotes[index] stubTo note
        }

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
        val searchQueryEntity = QueryFactory.makeQueryEntity("#1")
        searchQuery stubTo searchQueryEntity

        val searchedNote = listOf(NoteFactory.createNote(title = "testTitle#1"))
        val searchedRemoteNotes = emptyList<NoteEntity>()
        val searchedCacheNotes = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        searchedNote.forEachIndexed { index, note ->
            searchedCacheNotes[index] stubTo note
        }

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
        val updateNoteEntity = NoteFactory.createNoteEntity(title = "updateNote")

        updateNote stubTo updateNoteEntity
        noteCacheDataStore stubUpdateNote (updateNoteEntity to Completable.complete())

        whenUpdateNote(updateNote)
            .test()
            .assertComplete()
    }

    @Test
    fun whenUpdateNoteThenNoValue(){
        val updateNote = NoteFactory.createNote(title = "updateNote")
        val updateNoteEntity = NoteFactory.createNoteEntity(title = "updateNote")

        updateNote stubTo updateNoteEntity
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
        val deleteNoteEntity = NoteFactory.createNoteEntity(title = "deleteNote")

        deleteNote stubTo deleteNoteEntity
        noteCacheDataStore stubDeleteNote (deleteNoteEntity to Completable.complete())

        whenDataRepositoryDeleteNote(deleteNote)
            .test()
            .assertComplete()
            .assertNoValues()

        noteCacheDataStore.verifyDeleteNote(deleteNoteEntity)
    }

    @Test
    fun whenDeleteMultipleNotesThenCallOnlyCache(){
        val deleteNote = NoteFactory.createNoteList(0,3)
        val deleteNoteEntities = NoteFactory.createNoteEntityList(0,3)

        deleteNoteEntities.forEachIndexed { index, noteEntity ->
            deleteNote[index] stubTo noteEntity
        }
        noteCacheDataStore stubDeleteMultiNotes (deleteNoteEntities to Completable.complete())

        whenDataRepositoryDeleteMultiNotes(deleteNote)
            .test()
            .assertComplete()
            .assertNoValues()

        noteRemoteDataStore.verifyDeleteMultiNotes(deleteNoteEntities, never())
        noteCacheDataStore.verifyDeleteMultiNotes(deleteNoteEntities, times(1))
    }
}