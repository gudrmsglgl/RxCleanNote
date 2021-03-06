package com.cleannote.data.test.container.stub

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteRemoteDataStore

class DataStoreStubberContainer(
    private val rDataStore: NoteRemoteDataStore,
    private val cDataStore: NoteCacheDataStore
) : Stubber<DataStoreStubberContainer>() {
    val rDataStoreStubber: NoteRemoteDataStoreStubber = NoteRemoteDataStoreStubber(rDataStore)
    val cDataStoreStubber: NoteCacheDataStoreStubber = NoteCacheDataStoreStubber(cDataStore)
}
