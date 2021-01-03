package com.cleannote.data.source

import com.cleannote.data.repository.NoteDataStore
import javax.inject.Inject

open class NoteDataStoreFactory
@Inject
constructor(
    private val noteCacheDataStore: NoteCacheDataStore,
    private val noteRemoteDataStore: NoteRemoteDataStore
){
    open fun retrieveDataStore(isCached: Boolean): NoteDataStore =
        if (isCached)
            noteCacheDataStore
        else
            noteRemoteDataStore

    open fun retrieveCacheDataStore(): NoteDataStore = noteCacheDataStore

    open fun retrieveRemoteDataStore(): NoteDataStore = noteRemoteDataStore
}