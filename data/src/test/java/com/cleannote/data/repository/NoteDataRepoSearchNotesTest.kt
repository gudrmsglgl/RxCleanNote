package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteList
import com.cleannote.data.extensions.transQueryEntity
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyObject

class NoteDataRepoSearchNotesTest: BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase1_callRemoteStoreSearchNotCallCacheStoreSearch(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .searchNotes(defaultQuery.transQueryEntity())
            verify(cacheDataStore, never())
                .searchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: isCache false -> Remote SearchNotes -> saveNotes ")
    fun testCase1_verifyOrderingRemoteStoreSearchSave(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore)
                    .isCached(defaultQuery.page)
                cacheDataStore
                    .expectPageIsCached (defaultQuery.page to false)

                verify(remoteDataStore)
                    .searchNotes(defaultQuery.transQueryEntity())

                verify(cacheDataStore)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())

                verify(cacheDataStore, never())
                    .searchNotes(defaultQuery.transQueryEntity())

            }
        }
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: assertComplete ")
    fun testCase1_assertComplete(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: assertValue -> Remote Notes ")
    fun testCase1_assertValueRemoteNotes(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase2_callRemoteStoreSearchNotCallCacheStoreSearch(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .searchNotes(defaultQuery.transQueryEntity())
            verify(cacheDataStore, never())
                .searchNotes(defaultQuery.transQueryEntity())
        }
    }
    
    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: isCache false -> Remote SearchNotes -> saveNotes")
    fun testCase2_verifyOrderingRemoteStoreSearchSave(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore)
                    .isCached(defaultQuery.page)
                cacheDataStore
                    .expectPageIsCached (defaultQuery.page to false)

                verify(remoteDataStore)
                    .searchNotes(defaultQuery.transQueryEntity())

                verify(cacheDataStore)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())

                verify(cacheDataStore, never())
                    .searchNotes(defaultQuery.transQueryEntity())

            }
        }
    }
    
    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: AssertComplete")
    fun testCase2_assertComplete(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }
    
    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: AssertValue -> RemoteNotes")
    fun testCase2_assertValueRemoteNotes(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,10)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때 "){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }
    
    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ⭕")
    fun testCase3_callRemoteCacheDataStore(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,5)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때"){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .searchNotes(defaultQuery.transQueryEntity())
            verify(cacheDataStore)
                .searchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: isCache false -> Remote SearchNotes -> saveNotes -> Cache SearchNotes")
    fun testCase3_verifyOrderingRemoteSearchCacheSaveCacheSearch(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,5)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때"){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore)
                    .isCached(defaultQuery.page)
                cacheDataStore
                    .expectPageIsCached (defaultQuery.page to false)

                verify(remoteDataStore)
                    .searchNotes(defaultQuery.transQueryEntity())

                verify(cacheDataStore)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())

                verify(cacheDataStore)
                    .searchNotes(defaultQuery.transQueryEntity())

            }
        }
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: AssertComplete")
    fun testCase3_assertComplete(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,5)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때"){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: AssertValue -> CacheNotes")
    fun testCase3_assertValueCacheNotes(){
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0,5)
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때"){
                remoteDataStore {
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = false)
                    stubSaveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: RemoteDataStore SearchNotes ❌ CacheDataStore SearchNotes ⭕")
    fun testCase4_callCacheStoreSearchNotCallRemoteStoreSearch(){
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때"){
                remoteDataStore {

                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = true)
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            verify(cacheDataStore)
                .searchNotes(defaultQuery.transQueryEntity())
            verify(remoteDataStore, never())
                .searchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: isCache true -> Cache SearchNotes")
    fun testCase4_verifyOrderingCacheStoreIsCacheSearchNotes(){
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때"){
                remoteDataStore {

                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = true)
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore)
                    .isCached(defaultQuery.page)
                cacheDataStore
                    .expectPageIsCached (defaultQuery.page to true)

                verify(remoteDataStore, never())
                    .searchNotes(defaultQuery.transQueryEntity())

                verify(cacheDataStore, never())
                    .saveNotes(listOf(), defaultQuery.transQueryEntity())

                verify(cacheDataStore)
                    .searchNotes(defaultQuery.transQueryEntity())

            }
        }
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: AssertComplete")
    fun testCase4_assertComplete(){
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때"){
                remoteDataStore {

                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = true)
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: AssertValue -> CacheNotes")
    fun testCase4_assertValueCacheNotes(){
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때"){
                remoteDataStore {

                }
                cacheDataStore {
                    stubPageIsCache(param = defaultQuery.page, stub = true)
                    stubCurrentPageNoteSize(param = defaultQuery.page, stub = cacheNote.size)
                    stubSearchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase5_callRemoteDataStoreSearch(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSaveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .searchNotes(searchQuery.transQueryEntity())
            verify(cacheDataStore, never())
                .searchNotes(searchQuery.transQueryEntity())
        }

    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: RemoteStore Search -> CacheStore SaveNotes")
    fun testCase5_verifyOrderingRemoteSearchCacheSaveNotes(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSaveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore, never())
                    .isCached(searchQuery.page)

                verify(remoteDataStore)
                    .searchNotes(searchQuery.transQueryEntity())

                verify(cacheDataStore)
                    .saveNotes(remoteNote, searchQuery.transQueryEntity())

                verify(cacheDataStore, never())
                    .searchNotes(searchQuery.transQueryEntity())

            }
        }
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: AssertComplete")
    fun testCase5_assertComplete(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSaveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: AssertValue -> RemoteNotes")
    fun testCase5_assertValueRemoteNotes(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0,10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSaveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ⭕")
    fun testCase6_callRemoteCacheDataStore(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .searchNotes(searchQuery.transQueryEntity())
            verify(cacheDataStore)
                .searchNotes(searchQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: RemoteDataStore Search -> CacheDataStore Search")
    fun testCase6_verifyOrderingRemoteSearchCacheSearch(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        verifyContainer {
            inOrder(remoteDataStore, cacheDataStore){

                verify(cacheDataStore, never())
                    .isCached(searchQuery.page)

                verify(remoteDataStore)
                    .searchNotes(searchQuery.transQueryEntity())

                verify(cacheDataStore, never())
                    .saveNotes(remoteNote, searchQuery.transQueryEntity())

                verify(cacheDataStore)
                    .searchNotes(searchQuery.transQueryEntity())

            }
        }
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: AssertComplete")
    fun testCase6_assertComplete(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: AssertValue -> CacheNotes")
    fun testCase6_assertValueCacheNotes(){
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때"){
                remoteDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cacheDataStore {
                    stubSearchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

}