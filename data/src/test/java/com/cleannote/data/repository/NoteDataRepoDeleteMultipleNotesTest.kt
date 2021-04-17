package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteEntityList
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoDeleteMultipleNotesTest : BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase[Cache Complete]: Call CacheDataStore DeleteMultipleNotes")
    fun testCase_callCacheDataStore() {
        val selectedNotes = NoteFactory.createNoteList(0, 5)

        stubContainer {
            cDataStoreStubber.deleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()

        dataStoreVerifyScope {
            deleteMultipleNotes(selectedNotes.transNoteEntityList())
        }
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertComplete")
    fun testCase_assertComplete() {
        val selectedNotes = NoteFactory.createNoteList(0, 5)

        stubContainer {
            cDataStoreStubber.deleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertValue -> NoValue")
    fun testCase_assertValue() {
        val selectedNotes = NoteFactory.createNoteList(0, 5)

        stubContainer {
            cDataStoreStubber.deleteMultipleNotes(param = selectedNotes.transNoteEntityList(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteMultiNotes(selectedNotes)
            .test()
            .assertNoValues()
    }

    private fun whenDataRepositoryDeleteMultiNotes(notes: List<Note>) = noteDataRepository.deleteMultipleNotes(notes)
}
