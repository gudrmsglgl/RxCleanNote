package com.cleannote.presentation.notelist.multidelete.tester

import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat

class DeleteMultipleFeatureTester(
    private val viewModel: NoteListViewModel,
    private val usecase: DeleteMultipleNotes,
    private val deleteMultiCaptors: DeleteMultiUseCaseCaptors
) : ViewModelFeatureTester<DeleteMultipleFeatureTester, Nothing, List<Note>, NoteView>(deleteMultiCaptors) {

    fun deleteMultiNotes(param: List<NoteView>): DeleteMultipleFeatureTester {
        viewModel.deleteMultiNotes(param)
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): DeleteMultipleFeatureTester {
        usecase.verifyExecute(deleteMultiCaptors, deleteMultiCaptors.paramCaptor)
        return this
    }

    override fun vmCurrentData(): DataState<NoteView>? {
        return viewModel.deleteResult.value
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }

    override fun expectData(data: NoteView?): DeleteMultipleFeatureTester {
        // throw UnsupportedOperationException("DeleteMultipleNotes Usecase Don't Return deleteResult, replace function expectData() to expectTotalNotes()")
        return this
    }

    fun expectTotalNote(param: List<NoteView>): DeleteMultipleFeatureTester {
        assertThat(viewModel.noteList.value?.data, `is`(param))
        return this
    }
}
