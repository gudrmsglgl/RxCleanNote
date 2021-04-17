package com.cleannote.presentation.notelist.insert.tester

import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel

class InsertFeatureTester(
    private val viewModel: NoteListViewModel,
    private val useCase: InsertNewNote,
    private val insertCaptors: InsertUseCaseCaptors
) : ViewModelFeatureTester<InsertFeatureTester, Long, Note, NoteView>(insertCaptors) {

    fun insertNote(param: NoteView): InsertFeatureTester {
        viewModel.insertNotes(param)
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): InsertFeatureTester {
        useCase.verifyExecute(insertCaptors, insertCaptors.paramCaptor)
        return this
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }

    override fun vmCurrentData(): DataState<NoteView>? {
        return viewModel.insertResult.value
    }
}
