package com.cleannote.data.test.container.verify

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import org.mockito.Mock

class VerifierContainer(
    @Mock val factory: NoteDataStoreFactory,
    @Mock val remoteDataStore: NoteRemoteDataStore,
    @Mock val cacheDataStore: NoteCacheDataStore
): Verifier<VerifierContainer>()