package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.repository.NoteCache
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.data.test.factory.QueryFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.lang.invoke.MethodHandles.catchException

class NoteCacheDataStoreTest {

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteCache: NoteCache
    private lateinit var noteEntity: NoteEntity

    private val noteEntities = NoteFactory.createNoteEntityList(0,5)

    private val successInserted: Long = 1L
    @BeforeEach
    fun setUp() {
        noteEntity = NoteFactory.createNoteEntity("#1", "title#1","body#1")
        noteCache = mock{
            on { insertCacheNewNote(noteEntity)} doReturn Single.just(successInserted)
            on { saveNotes(noteEntities) }.thenReturn(Completable.complete())
            on { isCached(any()) } doReturn Single.just(true)
            on { updateNote(noteEntity) } doReturn Completable.complete()
            on { deleteNote(noteEntity)} doReturn Completable.complete()
        }
        noteCacheDataStore = NoteCacheDataStore(noteCache)
    }


    @Test
    fun insertCacheNewNoteCompletes(){
        val testObserver = noteCacheDataStore.insertCacheNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertCacheNewNoteReturnDataLong(){
        val testObserver = noteCacheDataStore.insertCacheNewNote(noteEntity).test()
        verify(noteCache).insertCacheNewNote(noteEntity)
        testObserver.assertValue(successInserted)
    }

    @Test
    fun insertRemoteNoteReturnThrow(){
        Assertions.assertThrows(UnsupportedOperationException::class.java, Executable {
            noteCacheDataStore.insertRemoteNewNote(noteEntity)
        })
    }

    @Test
    fun loginReturnThrow(){
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            noteCacheDataStore.login("")
        }
    }

    @Test
    fun saveNotesCompletes(){
        val testObserver =
            noteCacheDataStore.saveNotes(noteEntities, QueryFactory.makeQueryEntity()).test()
        testObserver.onComplete()
    }

    @Test
    fun isCacheCompletes(){
        val testObserver = noteCacheDataStore.isCached(1).test()
        testObserver.onComplete()
    }

    @Test
    fun updateNoteCallNoteCache(){
        whenCacheDataStoreUpdateNote(noteEntity).test()
        verifyCacheCallUpdateNote(noteEntity)
    }

    @Test
    fun updateNoteAssertComplete(){
        whenCacheDataStoreUpdateNote(noteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun deleteNoteCallNoteCache(){
        whenCacheDataStoreDeleteNote(noteEntity).test()
        verifyCacheCallDeleteNote(noteEntity)
    }

    @Test
    fun deleteNoteAssertComplete(){
        whenCacheDataStoreDeleteNote(noteEntity)
            .test()
            .assertComplete()
            .assertNoValues()
    }

    private fun whenCacheDataStoreDeleteNote(param: NoteEntity) =
        noteCacheDataStore.deleteNote(param)

    private fun verifyCacheCallDeleteNote(param: NoteEntity) {
        verify(noteCache).deleteNote(param)
    }

    private fun whenCacheDataStoreUpdateNote(param: NoteEntity) =
        noteCacheDataStore.updateNote(param)

    private fun verifyCacheCallUpdateNote(param: NoteEntity){
        verify(noteCache).updateNote(param)
    }

}


