package com.cleannote.data.source

import com.cleannote.data.repository.NoteCache
import com.cleannote.data.repository.NoteDataStore
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteDataStoreFactoryTest {
    private lateinit var factory: NoteDataStoreFactory

    private lateinit var noteCache: NoteCache
    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore

    @BeforeEach
    fun setUp() {
        noteCache = mock()
        noteCacheDataStore = mock()
        noteRemoteDataStore = mock()
        factory = NoteDataStoreFactory(noteCacheDataStore, noteRemoteDataStore)
    }

    @Test
    fun retrieveCacheDataStoreReturnsCacheDataStore() {
        val noteCacheDataStore = factory.retrieveCacheDataStore()
        assertThat(noteCacheDataStore, instanceOf(NoteCacheDataStore::class.java))
    }

    @Test
    fun retrieveRemoteDataStoreReturnsRemoteDataStore() {
        val remoteDataStore = factory.retrieveRemoteDataStore()
        assertThat(remoteDataStore, instanceOf(NoteRemoteDataStore::class.java))
    }

    @Test
    fun retrieveDataStoreReturnCacheDataStore() {
        stubCurrentPageIsCached(page = 0, stub = true)
        val dataStore = factory.retrieveDataStore(isCachedOnNoteCache(page = 0))
        assertThat(dataStore, instanceOf(NoteCacheDataStore::class.java))
    }

    @Test
    fun retrieveDataStoreReturnRemoteDataStore() {
        stubCurrentPageIsCached(page = 1, stub = false)
        val dataStore: NoteDataStore = factory.retrieveDataStore(isCachedOnNoteCache(page = 1))
        assertThat(dataStore, instanceOf(NoteRemoteDataStore::class.java))
    }

    private fun stubCurrentPageIsCached(page: Int, stub: Boolean) {
        whenever(noteCache.isCached(page)).thenReturn(Single.just(stub))
    }

    private fun isCachedOnNoteCache(page: Int): Boolean {
        return noteCache.isCached(page).blockingGet()
    }
}
