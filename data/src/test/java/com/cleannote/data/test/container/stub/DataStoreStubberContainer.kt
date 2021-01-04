package com.cleannote.data.test.container.stub

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteRemoteDataStore

class DataStoreStubberContainer(
    private val rDataStore: NoteRemoteDataStore,
    private val cDataStore: NoteCacheDataStore
): Stubber<DataStoreStubberContainer>() {
    val remoteDataStore: NoteRemoteDataStoreStubber = NoteRemoteDataStoreStubber(rDataStore)
    val cacheDataStore: NoteCacheDataStoreStubber = NoteCacheDataStoreStubber(cDataStore)
}