package com.cleannote.presentation.notedetail.delete

import com.cleannote.presentation.data.State
import com.cleannote.presentation.notedetail.NoteDetailViewModelTest
import com.cleannote.presentation.notedetail.delete.tester.DeleteFeatureTester
import com.cleannote.presentation.notedetail.delete.tester.DeleteUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class NoteDetailVMDeleteTest : NoteDetailViewModelTest() {

    private lateinit var deleteFeatureTester: DeleteFeatureTester
    private lateinit var captors: DeleteUseCaseCaptors

    private val noteView = NoteFactory.createNoteView(title = "testNote", body = "testBody", date = "1")

    @BeforeEach
    fun detailVMDeleteTestSetup() {
        captors = DeleteUseCaseCaptors()
        deleteFeatureTester = DeleteFeatureTester(viewModel, deleteNote, captors)
    }

    @Test
    fun deleteNoteExecuteUseCase() {
        with(deleteFeatureTester) {

            deleteNote(noteView)
                .verifyUseCaseExecute()
        }
    }

    @Test
    fun deleteNoteStateLoadingReturnNoData() {
        with(deleteFeatureTester) {

            deleteNote(noteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)
        }
    }

    @Test
    fun deleteNoteStateLoadingToSuccessReturnData() {
        with(deleteFeatureTester) {

            deleteNote(noteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)

            stubUseCaseOnComplete()
                .verifyChangeState(State.SUCCESS)
                .expectData(noteView)
        }
    }

    @Test
    fun deleteNoteStateErrorReturnThrowableNoData() {
        val throwable = RuntimeException()
        with(deleteFeatureTester) {

            deleteNote(noteView)
                .verifyUseCaseExecute()
                .verifyChangeState(State.LOADING)
                .expectData(null)

            stubUseCaseOnError(throwable)
                .verifyChangeState(State.ERROR)
                .expectError(throwable)
                .expectData(null)
        }
    }
}
