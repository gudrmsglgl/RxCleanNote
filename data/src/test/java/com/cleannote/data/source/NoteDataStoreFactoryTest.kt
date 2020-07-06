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
    private lateinit var noteDataStoreFactory: NoteDataStoreFactory

    private lateinit var noteCache: NoteCache
    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore

    @BeforeEach
    fun setUp(){
        noteCache = mock()
        noteCacheDataStore = mock()
        noteRemoteDataStore = mock()
        noteDataStoreFactory = NoteDataStoreFactory(noteCache, noteCacheDataStore, noteRemoteDataStore)
    }

    @Test
    fun retrieveCacheDataStoreReturnsCacheDataStore() {
        val noteCacheDataStore = noteDataStoreFactory.retrieveCacheDataStore()
        assertThat(noteCacheDataStore, instanceOf(NoteCacheDataStore::class.java))
    }

    @Test
    fun retrieveRemoteDataStoreReturnsRemoteDataStore(){
        val remoteDataStore = noteDataStoreFactory.retrieveRemoteDataStore()
        assertThat(remoteDataStore, instanceOf(NoteRemoteDataStore::class.java))
    }

    @Test
    fun retrieveDataStoreReturnCacheDataStore(){
        whenever(noteCache.isCached(0)).thenReturn(Single.just(true))
        val dataStore: NoteDataStore =
            noteDataStoreFactory.retrieveDataStore(noteCache.isCached(0).blockingGet())
        assertThat(dataStore, instanceOf(NoteCacheDataStore::class.java))
    }

    @Test
    fun retrieveDataStoreReturnRemoteDataStore(){
        whenever(noteCache.isCached(1)).thenReturn(Single.just(false))
        val dataStore: NoteDataStore =
            noteDataStoreFactory.retrieveDataStore(noteCache.isCached(1).blockingGet())
        assertThat(dataStore, instanceOf(NoteRemoteDataStore::class.java))
    }
}