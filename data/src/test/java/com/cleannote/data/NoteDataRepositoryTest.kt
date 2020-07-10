package com.cleannote.data

import com.cleannote.data.mapper.NoteMapper
import com.cleannote.data.mapper.QueryMapper
import com.cleannote.data.mapper.UserMapper
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

class NoteDataRepositoryTest {

    private lateinit var noteDataRepository: NoteDataRepository

    private lateinit var noteDataStoreFactory: NoteDataStoreFactory
    private lateinit var noteMapper: NoteMapper
    private lateinit var userMapper: UserMapper
    private lateinit var queryMapper: QueryMapper

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore
    private lateinit var noteEntities: List<NoteEntity>
    private lateinit var cacheNoteEntities: List<NoteEntity>

    private val userEntities = UserFactory.userEntities()

    private val insertSuccessNote = NoteFactory.createNoteEntity("#1","title#1","body#1")
    private val insertFailNote = NoteFactory.createNoteEntity("#2","title#2","body#2")
    private val successNote: Note = NoteFactory.createNote("#1","title#1","body#1")
    private val failNote: Note = NoteFactory.createNote("#2", "title#2", "body#2")
    private val defaultQuery = QueryFactory.makeQuery()
    private val defaultQueryEntity = QueryFactory.makeQueryEntity()

    private val insertedSuccess = 1L
    private val insertedFail = -1L

    @BeforeEach
    fun setUp(){
        queryMapper = mock{
            on { mapToEntity(defaultQuery) } doReturn defaultQueryEntity
        }
        noteMapper = mock(){
            on { mapToEntity(successNote) } doReturn insertSuccessNote
            on { mapToEntity(failNote) } doReturn insertFailNote
        }
        userMapper = mock ()
        noteEntities = NoteFactory.createNoteEntityList(0,10)
        cacheNoteEntities = NoteFactory.createNoteEntityList(0,10)
        noteCacheDataStore = mock {
            on { insertCacheNewNote(insertSuccessNote) } doReturn Single.just(insertedSuccess)
            on { insertCacheNewNote(insertFailNote) } doReturn Single.just(insertedFail)
            on { saveNotes(noteEntities, defaultQueryEntity) } doReturn Completable.complete()
            on { searchNotes(defaultQueryEntity) } doReturn Flowable.just(cacheNoteEntities)
            on { isCached(1) } doReturn Single.just(false)
            on { isCached(2) } doReturn Single.just(true)
            on { isCached(3) } doReturn Single.just( false )
        }
        noteRemoteDataStore = mock{
            on { getNumNotes() }.doReturn(Flowable.just(noteEntities))
            on {
                insertRemoteNewNote(insertSuccessNote)
                insertRemoteNewNote(insertFailNote)
            } doReturn Completable.complete()
            on { login(UserFactory.USER_ID) } doReturn Flowable.just(userEntities)
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
    fun getNumNoteComplete(){
        val notes: List<Note> = NoteFactory.createNoteList(0,10)
        // note 를 기준으로 잡고 assert 로 확인 가능 맵핑 됬는 지
        notes.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(noteEntities[index])).thenReturn(note)
        }
        val testObserver = noteDataRepository.getNumNotes().test()
        testObserver.assertComplete()
        testObserver.assertValue(notes)
    }

    @Test
    fun insertNewNoteComplete(){
        val testObserver = noteDataRepository.insertNewNote(successNote).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertNewNoteReturnSuccess(){
        val testObserver = noteDataRepository.insertNewNote(successNote).test()
        testObserver.assertValue(insertedSuccess)
    }

    @Test
    fun insertNewNoteReturnFail(){
        val testObserver = noteDataRepository.insertNewNote(failNote).test()
        testObserver.assertValue(insertedFail)
    }

    @Test
    fun loginComplete(){
        val test = noteDataRepository.login(UserFactory.USER_ID).test()
        test.assertComplete()
    }

    @Test
    fun loginReturnData(){
        val userList = UserFactory.users()
        userList.forEachIndexed { index, user ->
            whenever(userMapper.mapFromEntity(userEntities[index])).thenReturn(user)
        }
        val test = noteDataRepository.login(UserFactory.USER_ID).test()
        test.assertValue(userList)
    }

    @Test
    fun searchNotesComplete(){
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(cacheNoteEntities[index])).thenReturn(note)
        }
        val testObserver = noteDataRepository.searchNotes(defaultQuery).test()
        testObserver.assertComplete()
    }

    @Test
    fun searchNotesSaveCacheFromRemoteData(){
        val resultNote: List<Note> = NoteFactory.createNoteList(0,10)
        resultNote.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(cacheNoteEntities[index])).thenReturn(note)
        }
        val testObserver = noteDataRepository.searchNotes(defaultQuery).test()

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

        whenever(queryMapper.mapToEntity(searchQuery)).thenReturn(searchQueryEntity)
        whenever(noteRemoteDataStore.searchNotes(searchQueryEntity))
            .thenReturn(Flowable.just(searchedNoteEntities))
        whenever(noteCacheDataStore.saveNotes(searchedNoteEntities, searchQueryEntity))
            .thenReturn(Completable.complete())
        whenever(noteCacheDataStore.searchNotes(searchQueryEntity))
            .thenReturn(Flowable.just(searchedNoteEntities))
        searchedNote.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(searchedNoteEntities[index])).thenReturn(note)
        }

        verify(noteCacheDataStore, never()).isCached(any())

        val testObserver = noteDataRepository.searchNotes(searchQuery).test()
        testObserver.assertComplete()
        testObserver.assertValue(searchedNote)
    }


}