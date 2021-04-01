package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transNoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoDeleteNoteTest: BaseNoteRepositoryTest() {

    @Test
    @DisplayName("TestCase[Cache Complete]: Call CacheDataStore DeleteNote")
    fun testCase_callCacheDataStore(){
        val note = NoteFactory.createNote(id = "#1", title = "deleted")

        stubContainer {
            cDataStoreStubber.deleteNote(param = note.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteNote(note)
            .test()

        dataStoreVerifyScope {
            deleteNote(note.transNoteEntity())
        }
    }

    @Test
    @DisplayName("TestCase[Cache Complete]: AssertComplete")
    fun testCase_assertComplete(){
        val note = NoteFactory.createNote(id = "#1", title = "deleted")

        stubContainer {
            cDataStoreStubber.deleteNote(param = note.transNoteEntity(), stub = Completable.complete())
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
            cDataStoreStubber.deleteNote(param = note.transNoteEntity(), stub = Completable.complete())
        }

        whenDataRepositoryDeleteNote(note)
            .test()
            .assertNoValues()
    }

    private fun whenDataRepositoryDeleteNote(note: Note) = noteDataRepository.deleteNote(note)
}