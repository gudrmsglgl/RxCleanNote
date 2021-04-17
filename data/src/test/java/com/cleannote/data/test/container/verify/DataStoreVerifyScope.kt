package com.cleannote.data.test.container.verify

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Mock
import org.mockito.verification.VerificationMode

class DataStoreVerifyScope(
    @Mock val factory: NoteDataStoreFactory,
    @Mock val rDataStore: NoteRemoteDataStore,
    @Mock val cDataStore: NoteCacheDataStore
) {

    operator fun invoke(func: DataStoreVerifyScope.() -> Unit) {
        func(this)
    }

    fun expectPageIsCached(param: Pair<Int, Boolean>) =
        assertThat(cDataStore.isCached(param.first).blockingGet(), `is`(param.second))

    fun VerificationMode.remoteSearchNotes(param: QueryEntity) =
        verify(rDataStore, this).searchNotes(param)

    fun VerificationMode.cacheSearchNotes(param: QueryEntity) =
        verify(cDataStore, this).searchNotes(param)

    fun VerificationMode.isCached(param: Int) = verify(cDataStore, this).isCached(param)

    fun VerificationMode.saveNotes(
        rNotes: List<NoteEntity>,
        queryEntity: QueryEntity
    ) = verify(cDataStore, this).saveNotes(rNotes, queryEntity)

    fun VerificationMode.remoteInsertNote(param: NoteEntity) = verify(rDataStore, this).insertRemoteNewNote(param)
    fun cacheInsertNote(param: NoteEntity) = verify(cDataStore).insertCacheNewNote(param)

    fun updateNote(param: NoteEntity) = verify(cDataStore).updateNote(param)
    fun deleteNote(param: NoteEntity) = verify(cDataStore).deleteNote(param)
    fun deleteMultipleNotes(param: List<NoteEntity>) = verify(cDataStore).deleteMultipleNotes(param)
    fun nextPageExist(param: QueryEntity) = verify(cDataStore).nextPageExist(param)
}
