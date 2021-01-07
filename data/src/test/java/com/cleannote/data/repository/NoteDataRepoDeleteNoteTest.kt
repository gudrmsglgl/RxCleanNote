package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoDeleteNoteTest: BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase[Cache Complete]: Call CacheDataStore DeleteNote")
    fun testCase_callCacheDataStore(){
        val note = NoteFactory.createNote(id = "#1", title = "deleted")

        stubContainer {
            cacheDataStore.stubDeleteNote(param = note.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteNote(note)
            .test()

        verifyContainer {
            verify(cacheDataStore)
                .deleteNote(note.transNoteEntity())
        }
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertComplete")
    fun testCase_assertComplete(){
        val note = NoteFactory.createNote(id = "#1", title = "deleted")

        stubContainer {
            cacheDataStore.stubDeleteNote(param = note.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteNote(note)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertValue -> NoValue")
    fun testCase_assertValueNoValue(){
        val note = NoteFactory.createNote(id = "#1", title = "deleted")

        stubContainer {
            cacheDataStore.stubDeleteNote(param = note.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteNote(note)
            .test()
            .assertNoValues()
    }

}