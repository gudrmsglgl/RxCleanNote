package com.cleannote.presentation.notelist.next.tester

import com.cleannote.domain.interactor.usecases.notelist.NextPageExist
import com.cleannote.domain.model.Query
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.notelist.NoteListViewModel

class NextFeatureTester(
    private val viewModel: NoteListViewModel,
    private val useCase: NextPageExist,
    private val nextUseCaseCaptors: NextUseCaseCaptors
) : ViewModelFeatureTester<NextFeatureTester, Boolean, Query, Boolean>(nextUseCaseCaptors) {

    fun nextPageExist(): NextFeatureTester {
        viewModel.updateNextPageExist()
        setState(currentState())
        return this
    }

    override fun verifyUseCaseExecute(): NextFeatureTester {
        useCase.verifyExecute(nextUseCaseCaptors, nextUseCaseCaptors.paramCaptor)
        return this
    }

    override fun vmCurrentData(): DataState<Boolean>? {
        return viewModel.queryMgr.isNextPageExist()
    }

    override fun currentState(): State? = vmCurrentData()?.status
}
