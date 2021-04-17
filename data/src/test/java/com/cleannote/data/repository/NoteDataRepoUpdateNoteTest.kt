package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoUpdateNoteTest : BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase[Cache Complete]: Call CacheDataStore Update")
    fun testCase_callCacheDataStore() {
        val updatedNote = NoteFactory.createNote(id = "#1", title = "updatedTitle", body = "updatedBody")

        stubContainer {
            cDataStoreStubber.updateNote(param = updatedNote.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryUpdateNote(updatedNote)
            .test()

        dataStoreVerifyScope {
            updateNote(updatedNote.transNoteEntity())
        }
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertComplete")
    fun testCase_assertComplete() {
        val updatedNote = NoteFactory.createNote(id = "#1", title = "updatedTitle", body = "updatedBody")

        stubContainer {
            cDataStoreStubber.updateNote(param = updatedNote.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryUpdateNote(updatedNote)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertValue -> NoValue")
    fun testCase_assertValueNoValue() {
        val updatedNote = NoteFactory.createNote(id = "#1", title = "updatedTitle", body = "updatedBody")

        stubContainer {
            cDataStoreStubber.updateNote(param = updatedNote.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryUpdateNote(updatedNote)
            .test()
            .assertNoValues()
    }

    private fun whenDataRepositoryUpdateNote(note: Note) = noteDataRepository.updateNote(note)
}
