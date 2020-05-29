package com.cleannote.data.source

import com.cleannote.data.repository.NoteCache
import com.cleannote.data.repository.NoteDataStore
import javax.inject.Inject

open class NoteDataStoreFactory
@Inject
constructor(
    private val noteCache: NoteCache,
    private val noteCacheDataStore: NoteCacheDataStore,
    private val noteRemoteDataStore: NoteRemoteDataStore
){
    open fun retrieveCacheDataStore(): NoteCacheDataStore = noteCacheDataStore
    open fun retrieveRemoteDataStore(): NoteDataStore = noteRemoteDataStore
}