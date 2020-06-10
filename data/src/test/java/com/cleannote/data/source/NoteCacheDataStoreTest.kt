package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.repository.NoteCache
import com.cleannote.data.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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

    private val successInserted: Long = 1L
    @BeforeEach
    fun setUp() {
        noteEntity = NoteFactory.createNoteEntity("#1", "title#1","body#1")
        noteCache = mock{
            on { getNumNotes() } doReturn Flowable.just(NoteFactory.createNoteEntityList(10))
            on { insertCacheNewNote(noteEntity)} doReturn Single.just(successInserted)
        }
        noteCacheDataStore = NoteCacheDataStore(noteCache)
    }

    @Test
    fun getNumNotesCompletes() {
        val testObserver = noteCacheDataStore.getNumNotes().test()
        verify(noteCache).getNumNotes()
        testObserver.assertComplete()
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
}