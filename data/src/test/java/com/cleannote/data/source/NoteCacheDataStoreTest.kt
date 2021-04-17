package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.repository.NoteCache
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteCacheDataStoreTest {

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteCache: NoteCache
    private lateinit var noteEntity: NoteEntity

    private val noteEntities = NoteFactory.createNoteEntityList(0, 5)
    private val queryEntity = QueryFactory.makeQueryEntity()
    private val successInserted: Long = 1L
    @BeforeEach
    fun setUp() {
        noteEntity = NoteFactory.createNoteEntity("#1", "title#1", "body#1")
        noteCache = mock {
            on { insertCacheNewNote(noteEntity) } doReturn Single.just(successInserted)
            on { saveNotes(noteEntities) }.thenReturn(Completable.complete())
            on { isCached(any()) } doReturn Single.just(true)
            on { updateNote(noteEntity) } doReturn Completable.complete()
            on { deleteNote(noteEntity) } doReturn Completable.complete()
            on { deleteMultipleNotes(noteEntities) } doReturn Completable.complete()
            on { currentPageNoteSize(queryEntity) } doReturn Single.just(noteEntities.size)
            on { nextPageExist(queryEntity) } doReturn Single.just(true)
        }
        noteCacheDataStore = NoteCacheDataStore(noteCache)
    }

    @Test
    fun insertCacheNewNoteCallCacheFunc() {
        noteCacheDataStore.insertCacheNewNote(noteEntity).test()
        verify(noteCache).insertCacheNewNote(noteEntity)
    }

    @Test
    fun insertCacheNewNoteCompletes() {
        noteCacheDataStore.insertCacheNewNote(noteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun insertCacheNewNoteReturnRow() {
        noteCacheDataStore.insertCacheNewNote(noteEntity)
            .test()
            .assertValue(successInserted)
    }

    @Test
    fun insertRemoteNoteReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteCacheDataStore.insertRemoteNewNote(noteEntity)
        }
    }

    @Test
    fun loginReturnThrow() {
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteCacheDataStore.login("")
        }
    }

    @Test
    fun saveNotesCallCacheFunc() {
        noteCacheDataStore.saveNotes(noteEntities, QueryFactory.makeQueryEntity()).test()
        verify(noteCache).saveNotes(noteEntities)
    }

    @Test
    fun saveNotesCompletes() {
        noteCacheDataStore
            .saveNotes(noteEntities, QueryFactory.makeQueryEntity())
            .test()
            .onComplete()
    }

    @Test
    fun saveNotesReturnNoValue() {
        noteCacheDataStore
            .saveNotes(noteEntities, QueryFactory.makeQueryEntity())
            .test()
            .assertNoValues()
    }

    @Test
    fun isCacheCallCacheFunc() {
        noteCacheDataStore.isCached(1).test()
        verify(noteCache).isCached(1)
    }

    @Test
    fun isCacheComplete() {
        noteCacheDataStore.isCached(1)
            .test()
            .assertComplete()
    }

    @Test
    fun isCacheReturnBoolean() {
        noteCacheDataStore.isCached(1)
            .test()
            .assertValue(true)
    }

    @Test
    fun updateNoteCallCacheFunc() {
        noteCacheDataStore.updateNote(noteEntity).test()
        verify(noteCache).updateNote(noteEntity)
    }

    @Test
    fun updateNoteComplete() {
        noteCacheDataStore.updateNote(noteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun updateNoteReturnNoValue() {
        noteCacheDataStore.updateNote(noteEntity)
            .test()
            .assertNoValues()
    }

    @Test
    fun deleteNoteCallCacheFunc() {
        noteCacheDataStore.deleteNote(noteEntity).test()
        verify(noteCache).deleteNote(noteEntity)
    }

    @Test
    fun deleteNoteComplete() {
        noteCacheDataStore.deleteNote(noteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun deleteNoteReturnNoValue() {
        noteCacheDataStore.deleteNote(noteEntity)
            .test()
            .assertNoValues()
    }

    @Test
    fun deleteMultipleNotesCallCacheFunc() {
        noteCacheDataStore.deleteMultipleNotes(noteEntities).test()
        verify(noteCache).deleteMultipleNotes(noteEntities)
    }

    @Test
    fun deleteMultipleNotesComplete() {
        noteCacheDataStore.deleteMultipleNotes(noteEntities)
            .test()
            .assertComplete()
    }

    @Test
    fun deleteMultipleNotesReturnNoValue() {
        noteCacheDataStore.deleteMultipleNotes(noteEntities)
            .test()
            .assertNoValues()
    }

    @Test
    fun currentPageNoteSizeCallCacheFunc() {
        noteCacheDataStore.currentPageNoteSize(queryEntity).test()
        verify(noteCache).currentPageNoteSize(queryEntity)
    }

    @Test
    fun currentPageNoteSizeComplete() {
        noteCacheDataStore.currentPageNoteSize(queryEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun currentPageNoteSizeReturnNoteSize() {
        noteCacheDataStore.currentPageNoteSize(queryEntity)
            .test()
            .assertValue(noteEntities.size)
    }

    @Test
    fun nextPageExistCallCacheFunc() {
        noteCacheDataStore
            .nextPageExist(queryEntity)
            .test()
        verify(noteCache).nextPageExist(queryEntity)
    }

    @Test
    fun nextPageExistComplete() {
        noteCacheDataStore
            .nextPageExist(queryEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun nextPageExistReturnValueBool() {
        noteCacheDataStore
            .nextPageExist(queryEntity)
            .test()
            .assertValue(true)
    }
}
