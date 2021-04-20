package com.cleannote.presentation.notelist.insert

import com.cleannote.presentation.data.State
import com.cleannote.presentation.notelist.NoteListViewModelTest
import com.cleannote.presentation.notelist.insert.tester.InsertFeatureTester
import com.cleannote.presentation.notelist.insert.tester.InsertUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteListVMInsertTest : NoteListViewModelTest() {

    lateinit var captors: InsertUseCaseCaptors
    lateinit var insertFeatureTester: InsertFeatureTester

    @BeforeEach
    fun deleteSetup() {
        captors = InsertUseCaseCaptors()
        insertFeatureTester = InsertFeatureTester(noteListViewModel, useCases.insertNewNote, captors)
    }

    @Test
    fun insertNoteExecuteUseCase() {
        val insertedNoteView = NoteFactory.createNoteView(title = "insertTitle", date = "1")
        with(insertFeatureTester) {
            insertNote(insertedNoteView)
                .verifyUseCaseExecute()
        }
    }

    @Test
    fun insertNoteStateLoadingReturnNoData() {
        val insertedNoteView = NoteFactory.createNoteView(title = "insertTitle", date = "1")
        with(insertFeatureTester) {
            insertNote(insertedNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)
        }
    }

    @Test
    fun insertNoteStateLoadingToSuccessReturnData() {
        val insertedNoteView = NoteFactory.createNoteView(title = "insertTitle", date = "1")
        with(insertFeatureTester) {

            insertNote(insertedNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)

            stubUseCaseOnSuccess(1L)
                .verifyChangeState(State.SUCCESS)
                .expectData(insertedNoteView)
        }
    }

    @Test
    fun insertNoteStateErrorReturnThrowableNoData() {
        val throwable = RuntimeException()
        val insertedNoteView = NoteFactory.createNoteView(title = "insertTitle", date = "1")
        with(insertFeatureTester) {

            insertNote(insertedNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)

            stubUseCaseOnError(throwable)
                .verifyChangeState(State.ERROR)
                .expectData(null)
                .expectError(throwable)
        }
    }
}
