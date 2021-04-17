package com.cleannote.presentation.notedetail.delete.tester

import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notedetail.NoteDetailViewModel

class DeleteFeatureTester(
    private val viewModel: NoteDetailViewModel,
    private val deleteNote: DeleteNote,
    private val deleteCaptors: DeleteUseCaseCaptors
) : ViewModelFeatureTester<DeleteFeatureTester, Nothing, Note, NoteView>(deleteCaptors) {

    fun deleteNote(param: NoteView): DeleteFeatureTester {
        viewModel.deleteNote(param)
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): DeleteFeatureTester {
        deleteNote.verifyExecute(deleteCaptors, deleteCaptors.param)
        return this
    }

    override fun vmCurrentData(): DataState<NoteView>? {
        return viewModel.deletedNote.value
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }
}
