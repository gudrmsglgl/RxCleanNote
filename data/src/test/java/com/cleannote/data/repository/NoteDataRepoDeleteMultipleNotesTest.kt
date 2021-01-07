package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteEntityList
import com.cleannote.data.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoDeleteMultipleNotesTest: BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase[Cache Complete]: Call CacheDataStore DeleteMultipleNotes")
    fun testCase_callCacheDataStore(){
        val selectedNotes = NoteFactory.createNoteList(0,5)

        stubContainer {
            cacheDataStore.stubDeleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()

        verifyContainer {
            verify(cacheDataStore)
                .deleteMultipleNotes(selectedNotes.transNoteEntityList())
        }
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertComplete")
    fun testCase_assertComplete(){
        val selectedNotes = NoteFactory.createNoteList(0,5)

        stubContainer {
            cacheDataStore.stubDeleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertValue -> NoValue")
    fun testCase_assertValue(){
        val selectedNotes = NoteFactory.createNoteList(0,5)

        stubContainer {
            cacheDataStore.stubDeleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()
            .assertNoValues()
    }

}