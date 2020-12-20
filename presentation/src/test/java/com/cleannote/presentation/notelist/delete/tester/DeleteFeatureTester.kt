package com.cleannote.presentation.notelist.delete.tester

import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel


class DeleteFeatureTester(
    private val viewModel: NoteListViewModel,
    private val usecase: DeleteNote,
    private val deleteCaptor: DeleteUseCaseCaptors
): ViewModelFeatureTester<DeleteFeatureTester, Nothing, Note, NoteView>(deleteCaptor) {

    fun delete(param: NoteView): DeleteFeatureTester {
        viewModel.deleteNote(param)
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): DeleteFeatureTester {
        usecase.verifyExecute(deleteCaptor, deleteCaptor.paramCaptor)
        return this
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }

    override fun vmCurrentData(): DataState<NoteView>? {
        return viewModel.deleteResult.value
    }

}
