package com.cleannote.presentation.notelist.delete.Tester

import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.transNoteView
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat


class DeleteFeatureTester(
    private val viewModel: NoteListViewModel,
    private val usecase: DeleteNote,
    private val captors: DeleteUseCaseCaptors
): ViewModelFeatureTester<Note, NoteView>() {

    fun delete(param: NoteView): DeleteFeatureTester {
        viewModel.deleteNote(param)
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): DeleteFeatureTester {
        usecase.verifyExecute(captors.onSuccessCaptor, captors.onErrorCaptor, captors.afterFinishedCaptor, captors.onCompleteCaptor, captors.paramCaptors)
        return this
    }

    override fun expectData(data: NoteView?): DeleteFeatureTester {
        if (data == null)
            assertThat(viewModel.deleteResult.value?.data, `is`(nullValue()))
        else
            assertThat(viewModel.deleteResult.value?.data, `is`(data))
        return this
    }

    override fun expectError(data: Throwable?): DeleteFeatureTester {
        assertThat(viewModel.deleteResult.value?.throwable, `is`(data))
        return this
    }

    override fun expectState(state: State): DeleteFeatureTester {
        assertThat(currentState(), `is`(state))
        return this
    }

    override fun stubUseCaseOnSuccess(stub: Note): DeleteFeatureTester {
        throw UnsupportedOperationException("DeleteNote UseCase don't have onSuccess Param")
        return this
    }

    override fun stubUseCaseOnError(stub: Throwable): DeleteFeatureTester {
        captors.onErrorCapturing(stub)
        setState(currentState())
        return this
    }

    override fun stubUseCaseOnComplete(): DeleteFeatureTester {
        captors.onCompleteCapturing()
        setState(currentState())
        return this
    }

    override fun currentState(): State? {
        return viewModel.deleteResult.value?.status
    }
}
