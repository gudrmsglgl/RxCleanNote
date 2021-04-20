package com.cleannote.presentation.notelist.delete

import com.cleannote.presentation.data.State
import com.cleannote.presentation.notelist.NoteListViewModelTest
import com.cleannote.presentation.notelist.delete.tester.DeleteFeatureTester
import com.cleannote.presentation.notelist.delete.tester.DeleteUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteListVMDeleteTest : NoteListViewModelTest() {

    lateinit var captors: DeleteUseCaseCaptors
    lateinit var deleteFeatureTester: DeleteFeatureTester

    @BeforeEach
    fun deleteSetup() {
        captors = DeleteUseCaseCaptors()
        deleteFeatureTester = DeleteFeatureTester(noteListViewModel, useCases.deleteNote, captors)
    }

    @Test
    fun deleteNoteExecuteUseCase() {
        val deleteNoteView = NoteFactory.createNoteView(title = "deleted", date = "1")

        with(deleteFeatureTester) {
            delete(deleteNoteView)
                .verifyUseCaseExecute()
        }
    }

    @Test
    fun deleteNoteStateLoadingReturnNoData() {
        val deleteNoteView = NoteFactory.createNoteView(title = "deleted", date = "1")

        with(deleteFeatureTester) {
            delete(deleteNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)
        }
    }

    @Test
    fun deleteNoteStateLoadingToSuccessReturnData() {
        val deleteNoteView = NoteFactory.createNoteView(title = "deleted", date = "1")

        with(deleteFeatureTester) {

            delete(deleteNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)

            stubUseCaseOnComplete()
                .verifyChangeState(State.SUCCESS)
                .expectData(deleteNoteView)
        }
    }

    @Test
    fun deleteNoteStateErrorReturnThrowableNoData() {
        val throwable = RuntimeException()
        val deleteNoteView = NoteFactory.createNoteView(title = "deleted", date = "1")

        with(deleteFeatureTester) {

            delete(deleteNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)

            stubUseCaseOnError(throwable)
                .verifyChangeState(State.ERROR)
                .expectData(null)
                .expectError(throwable)
        }
    }
}
