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

    private lateinit var noteEntities: List<NoteEntity>
    private lateinit var cacheNoteEntities: List<NoteEntity>
    private val defaultQuery = QueryFactory.makeQuery()
    private val defaultQueryEntity = QueryFactory.makeQueryEntity()

    @BeforeEach
    fun setUp(){
        queryMapper = mock(){
            on { mapToEntity(defaultQuery) } doReturn defaultQueryEntity
        }
        noteMapper = mock()
        userMapper = mock ()
        noteEntities = NoteFactory.createNoteEntityList(0,10)
        cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)
        noteCacheDataStore = mock {
            on { saveNotes(noteEntities, defaultQueryEntity) } doReturn Completable.complete()
            on { searchNotes(defaultQueryEntity) } doReturn Flowable.just(cacheNoteEntities)
            on { isCached(1) } doReturn Single.just(false)
            on { isCached(2) } doReturn Single.just(true)
            on { isCached(3) } doReturn Single.just( false )
        }
        noteRemoteDataStore = mock{
            on { searchNotes(defaultQueryEntity) } doReturn Flowable.just(noteEntities)
        }
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

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(resultNote)
    }

    @Test
    fun whenSearchNotesSaveCacheFromRemoteData(){
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            cacheNoteEntities[index] stubTo note
        }
        val testObserver = whenDataRepositorySearchNotes(defaultQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory) {
            verify(noteCacheDataStore).isCached(defaultQueryEntity.page)
            assertThat(noteCacheDataStore.isCached(defaultQueryEntity.page).blockingGet(), `is`(false))

            verify(noteDataStoreFactory).retrieveDataStore(false)
            assertThat(
                noteDataStoreFactory.retrieveDataStore(false),
                instanceOf(NoteRemoteDataStore::class.java)
            )

            verify(noteRemoteDataStore).searchNotes(defaultQueryEntity)
            verify(noteCacheDataStore).saveNotes(noteEntities, defaultQueryEntity)

            verify(noteCacheDataStore).searchNotes(defaultQueryEntity)
        }
        testObserver.assertValue(resultNote)
    }
    /*
    @Test
    fun searchNotesNotCallRemoteOnlyCacheData(){
        val nextPageQuery = QueryFactory.makeQuery(page = 2)
        val nextPageQueryEntity = QueryFactory.makeQueryEntity(page = 2)
        val nextCachedNoteEntities = NoteFactory.createNoteEntityList(10,20)
        whenever(queryMapper.mapToEntity(nextPageQuery)).thenReturn(nextPageQueryEntity)

        val resultNote: List<Note> = NoteFactory.createNoteList(10,20)
        resultNote.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(nextCachedNoteEntities[index])).thenReturn(note)
        }

        whenever(noteCacheDataStore.searchNotes(nextPageQueryEntity))
            .thenReturn(Flowable.just(nextCachedNoteEntities))

        val testObserver = noteDataRepository.searchNotes(nextPageQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory){
            verify(noteCacheDataStore).isCached(nextPageQueryEntity.page)
            assertThat(noteCacheDataStore.isCached(nextPageQueryEntity.page).blockingGet(), `is`(true))

            verify(noteDataStoreFactory).retrieveDataStore(true)
            assertThat(
                noteDataStoreFactory.retrieveDataStore(true),
                instanceOf(NoteCacheDataStore::class.java)
            )

            verify(noteRemoteDataStore, never()).searchNotes(nextPageQueryEntity)
            verify(noteCacheDataStore, never()).saveNotes(any(), any())

            verify(noteCacheDataStore).searchNotes(nextPageQueryEntity)
        }

        testObserver.assertValue(resultNote)
    }

    @Test
    fun searchNotesNextPageNoData(){
        val nextPageQuery = QueryFactory.makeQuery(page = 3)
        val nextPageQueryEntity = QueryFactory.makeQueryEntity(page = 3)
        val nextRemoteNoteEntities = emptyList<NoteEntity>()
        val nextCachedNoteEntities = emptyList<NoteEntity>()
        whenever(queryMapper.mapToEntity(nextPageQuery)).thenReturn(nextPageQueryEntity)

        val resultNote: List<Note> = emptyList()
        resultNote.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(nextCachedNoteEntities[index])).thenReturn(note)
        }

        whenever(noteRemoteDataStore.searchNotes(nextPageQueryEntity))
            .thenReturn(Flowable.just(nextRemoteNoteEntities))

        whenever(noteCacheDataStore.searchNotes(nextPageQueryEntity))
            .thenReturn(Flowable.just(nextCachedNoteEntities))

        val testObserver = noteDataRepository.searchNotes(nextPageQuery).test()

        inOrder(noteRemoteDataStore, noteCacheDataStore, noteDataStoreFactory){
            verify(noteCacheDataStore).isCached(nextPageQueryEntity.page)
            assertThat(noteCacheDataStore.isCached(nextPageQueryEntity.page).blockingGet(), `is`(false))

            verify(noteDataStoreFactory).retrieveDataStore(false)
            assertThat(
                noteDataStoreFactory.retrieveDataStore(false),
                instanceOf(NoteRemoteDataStore::class.java)
            )

            verify(noteRemoteDataStore).searchNotes(nextPageQueryEntity)
            verify(noteCacheDataStore, never()).saveNotes(any(), any())

            verify(noteCacheDataStore).searchNotes(nextPageQueryEntity)
        }

        testObserver.assertValue(resultNote)
    }

    @Test
    fun searchKeywordNoteComplete(){
        val searchedNote = listOf(NoteFactory.createNote(title = "testTitle#1"))
        val searchedNoteEntities = listOf(NoteFactory.createNoteEntity(title = "testTitle#1"))
        val searchQuery = QueryFactory.makeQuery("#1")
        val searchQueryEntity = QueryFactory.makeQueryEntity("#1")

        searchQuery stubTo searchQueryEntity
        noteRemoteDataStore stubSearchNotes (searchQueryEntity to searchedNoteEntities)
        noteCacheDataStore stubSaveNotes (Triple(searchedNoteEntities, searchQueryEntity, Completable.complete()))
        noteCacheDataStore stubSearchNotes (searchQueryEntity to searchedNoteEntities)

        searchedNote.forEachIndexed { index, note ->
            searchedNoteEntities[index] stubTo note
        }

        verify(noteCacheDataStore, never()).isCached(any())

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(searchedNote)
            .assertComplete()

    }*/

    @Test
    fun updateNoteComplete(){
        val updateNote = NoteFactory.createNote(title = "updateNote")
        val updateNoteEntity = NoteFactory.createNoteEntity(title = "updateNote")

        updateNote stubTo updateNoteEntity
        noteCacheDataStore stubUpdateNote (updateNoteEntity to Completable.complete())

        whenUpdateNote(updateNote)
            .test()
            .assertComplete()
    }

}