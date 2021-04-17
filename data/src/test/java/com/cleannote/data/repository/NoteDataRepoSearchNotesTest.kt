package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteList
import com.cleannote.data.extensions.transQueryEntity
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoSearchNotesTest : BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase1_callRemoteStoreSearchNotCallCacheStoreSearch() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteSearchNotes(defaultQuery.transQueryEntity())
            never()
                .cacheSearchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: Cache isCache[false] -> Remote SearchNotes -> Cache SaveNotes")
    fun testCase1_verifyOrderingRemoteStoreSearchSave() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                times(1).isCached(defaultQuery.page)
                expectPageIsCached(defaultQuery.page to false)
                times(1)
                    .remoteSearchNotes(defaultQuery.transQueryEntity())
                times(1)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())
                never()
                    .cacheSearchNotes(defaultQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: AssertComplete")
    fun testCase1_assertComplete() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase1[query: defaultQuery, isCache: false, remote notes:10 > cache notes:5]: AssertValue -> Remote Notes")
    fun testCase1_assertValueRemoteNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 5)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 클 때 ") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase2_callRemoteStoreSearchNotCallCacheStoreSearch() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteSearchNotes(defaultQuery.transQueryEntity())
            never()
                .cacheSearchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: Cache isCache[false] -> Remote SearchNotes -> Cache SaveNotes")
    fun testCase2_verifyOrderingRemoteStoreSearchSave() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                times(1).isCached(defaultQuery.page)
                expectPageIsCached(defaultQuery.page to false)
                times(1)
                    .remoteSearchNotes(defaultQuery.transQueryEntity())
                times(1)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())
                never()
                    .cacheSearchNotes(defaultQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: AssertComplete")
    fun testCase2_assertComplete() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase2[query: defaultQuery, isCache: false, remote notes:10 >= cache notes:10]: AssertValue -> RemoteNotes")
    fun testCase2_assertValueRemoteNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 같거나 클 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ⭕")
    fun testCase3_callRemoteCacheDataStore() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 5)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteSearchNotes(defaultQuery.transQueryEntity())
            times(1)
                .cacheSearchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: Cache isCache[false] -> Remote SearchNotes -> Cache SaveNotes -> Cache SearchNotes")
    fun testCase3_verifyOrderingRemoteSearchCacheSaveCacheSearch() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 5)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                times(1).isCached(defaultQuery.page)
                expectPageIsCached(defaultQuery.page to false)
                times(1)
                    .remoteSearchNotes(defaultQuery.transQueryEntity())
                times(1)
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())
                times(1)
                    .cacheSearchNotes(defaultQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: AssertComplete")
    fun testCase3_assertComplete() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 5)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase3[query: defaultQuery, isCache: false, remote notes: 5 < cache notes: 10]: AssertValue -> CacheNotes")
    fun testCase3_assertValueCacheNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = NoteFactory.createNoteEntityList(0, 5)
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하고 현재 page의 Cache 데이터보다 사이즈가 작을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    saveNotes(remoteNote, defaultQuery.transQueryEntity(), Completable.complete())
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: Call RemoteDataStore SearchNotes ❌ CacheDataStore SearchNotes ⭕")
    fun testCase4_callCacheStoreSearchNotCallRemoteStoreSearch() {
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때") {
                rDataStoreStubber {}
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = true)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            never()
                .remoteSearchNotes(defaultQuery.transQueryEntity())
            times(1)
                .cacheSearchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: isCache true -> Cache SearchNotes")
    fun testCase4_verifyOrderingCacheStoreIsCacheSearchNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때") {
                rDataStoreStubber {}
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = true)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                times(1).isCached(defaultQuery.page)
                expectPageIsCached(defaultQuery.page to true)
                never()
                    .remoteSearchNotes(defaultQuery.transQueryEntity())
                never()
                    .saveNotes(listOf(), defaultQuery.transQueryEntity())
                times(1)
                    .cacheSearchNotes(defaultQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: AssertComplete")
    fun testCase4_assertComplete() {
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때") {
                rDataStoreStubber {}
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = true)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase4[query: defaultQuery, isCache: true]: AssertValue -> CacheNotes")
    fun testCase4_assertValueCacheNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("현재 query page가 캐쉬 데이터를 로드해야 할 때") {
                rDataStoreStubber {}
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = true)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ❌")
    fun testCase5_callRemoteDataStoreSearch() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    saveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteSearchNotes(searchQuery.transQueryEntity())
            never()
                .cacheSearchNotes(searchQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: RemoteStore Search -> CacheStore SaveNotes")
    fun testCase5_verifyOrderingRemoteSearchCacheSaveNotes() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    saveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                never()
                    .isCached(searchQuery.page)
                times(1)
                    .remoteSearchNotes(searchQuery.transQueryEntity())
                times(1)
                    .saveNotes(remoteNote, searchQuery.transQueryEntity())
                never()
                    .cacheSearchNotes(searchQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: AssertComplete")
    fun testCase5_assertComplete() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    saveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase5[query: {like: 'keyword'}, remote notes not empty]: AssertValue -> RemoteNotes")
    fun testCase5_assertValueRemoteNotes() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재할 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    saveNotes(remoteNote, searchQuery.transQueryEntity(), Completable.complete())
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(remoteNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ⭕")
    fun testCase6_callRemoteCacheDataStore() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        dataStoreVerifyScope {
            verify(rDataStore)
                .searchNotes(searchQuery.transQueryEntity())
            verify(cDataStore)
                .searchNotes(searchQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: RemoteDataStore Search -> CacheDataStore Search")
    fun testCase6_verifyOrderingRemoteSearchCacheSearch() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                never()
                    .isCached(searchQuery.page)
                times(1)
                    .remoteSearchNotes(searchQuery.transQueryEntity())
                never()
                    .saveNotes(remoteNote, searchQuery.transQueryEntity())
                times(1)
                    .cacheSearchNotes(searchQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: AssertComplete")
    fun testCase6_assertComplete() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase6[query: {like: 'keyword'}, remote notes empty]: AssertValue -> CacheNotes")
    fun testCase6_assertValueCacheNotes() {
        val searchQuery = QueryFactory.makeQuery(search = "#todo")
        val remoteNote: List<NoteEntity> = emptyList()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("검색어로 Search -> Remote Note 존재하지 않을 때") {
                rDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    searchNotes(param = searchQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(searchQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    @Test
    @DisplayName("TestCase7[query: defaultQuery, isCache: false, remote notes: empty]: Call RemoteDataStore SearchNotes ⭕ CacheDataStore SearchNotes ⭕")
    fun testCase7_callRemoteCacheDataStoreSearch() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = emptyList<NoteEntity>()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하는데 데이터가 없을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteSearchNotes(defaultQuery.transQueryEntity())
            times(1)
                .cacheSearchNotes(defaultQuery.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase7[query: defaultQuery, isCache: false, remote notes: empty]: CacheStore isCache[false] -> RemoteStore SearchNotes -> CacheStore SearchNotes")
    fun testCase7_verifyOrderingFirstCacheStoreNextRemoteStore() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = emptyList<NoteEntity>()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하는데 데이터가 없을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()

        dataStoreVerifyScope {
            inOrder(rDataStore, cDataStore) {
                times(1).isCached(defaultQuery.page)
                expectPageIsCached(defaultQuery.page to false)
                times(1)
                    .remoteSearchNotes(defaultQuery.transQueryEntity())
                never()
                    .saveNotes(remoteNote, defaultQuery.transQueryEntity())
                times(1)
                    .cacheSearchNotes(defaultQuery.transQueryEntity())
            }
        }
    }

    @Test
    @DisplayName("TestCase7[query: defaultQuery, isCache: false, remote notes: empty]: AssertComplete")
    fun testCase7_assertComplete() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = emptyList<NoteEntity>()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하는데 데이터가 없을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase7[query: defaultQuery, isCache: false, remote notes: empty]: AssertValue -> CacheNotes")
    fun testCase7_assertValueCacheNotes() {
        val defaultQuery = QueryFactory.makeQuery()
        val remoteNote = emptyList<NoteEntity>()
        val cacheNote = NoteFactory.createNoteEntityList(0, 10)

        stubContainer {
            scenario("remote 를 로드해야 하는데 데이터가 없을 때") {
                rDataStoreStubber {
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = remoteNote)
                }
                cDataStoreStubber {
                    pageIsCache(param = defaultQuery.page, stub = false)
                    currentPageNoteSize(param = defaultQuery.transQueryEntity(), stub = cacheNote.size)
                    searchNotes(param = defaultQuery.transQueryEntity(), stub = cacheNote)
                }
            }
        }

        whenDataRepositorySearchNotes(defaultQuery)
            .test()
            .assertValue(cacheNote.transNoteList())
    }

    private fun whenDataRepositorySearchNotes(query: Query) = noteDataRepository.searchNotes(query)
}
