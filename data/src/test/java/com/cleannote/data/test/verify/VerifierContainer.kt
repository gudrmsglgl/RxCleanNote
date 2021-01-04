package com.cleannote.data.test.verify

import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import org.mockito.InOrder
import org.mockito.Mock

class VerifierContainer(
    @Mock val factory: NoteDataStoreFactory,
    @Mock val remoteDataStore: NoteRemoteDataStore,
    @Mock val cacheDataStore: NoteCacheDataStore
): Verifier<VerifierContainer>()