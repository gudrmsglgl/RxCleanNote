package com.cleannote.presentation.notelist.search.Tester

import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.transNoteViews
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat

class SearchFeatureTester(
    private val viewModel: NoteListViewModel,
    private val usecase: SearchNotes,
    private val captors: SearchUseCaseCaptors
): ViewModelFeatureTester<List<Note>, List<NoteView>>()
{

    override fun verifyUseCaseExecute(): SearchFeatureTester {
        usecase.verifyExecute(captors.onSuccessCaptor, captors.onErrorCaptor, captors.afterFinishedCaptor, captors.onCompleteCaptor, captors.queryCaptor)
        return this
    }

    override fun expectData(data: List<NoteView>?): SearchFeatureTester {
        if (data == null)
            assertThat(viewModel.noteList.value?.data, `is`(nullValue()))
        else
            assertThat(viewModel.noteList.value?.data, `is`(data))
        return this
    }

    override fun expectState(state: State): SearchFeatureTester {
        assertThat(currentState(), `is`(state))
        return this
    }

    override fun expectError(data: Throwable?): SearchFeatureTester {
        assertThat(viewModel.noteList.value?.throwable, `is`(data))
        return this
    }

    override fun stubUseCaseOnSuccess(stub: List<Note>): SearchFeatureTester {
        captors.onSuccessCapturing(stub)
        setState(currentState())
        return this
    }

    override fun stubUseCaseOnError(stub: Throwable): SearchFeatureTester {
        captors.onErrorCapturing(stub)
        setState(currentState())
        return this
    }

    override fun stubUseCaseOnComplete(): SearchFeatureTester {
        captors.onCompleteCapturing()
        setState(currentState())
        return this
    }

    fun search(isNextPage: Boolean = false): SearchFeatureTester {
        if (isNextPage) viewModel.nextPage()
        viewModel.searchNotes()
        setState(currentState())
        return this
    }

    fun setOrdering(order: String): SearchFeatureTester {
        viewModel.setOrdering(order)
        return this
    }

    fun searchKeyword(keyword: String): SearchFeatureTester {
        viewModel.searchKeyword(keyword)
        return this
    }

    fun expectQuery(
        order: String? = null,
        page: Int? = null,
        like: String? = null,
        sort: String? = null
    ): SearchFeatureTester {
        if (order != null)
            assertThat(captors.capturedQuery().order, `is`(order))
        else if (page != null)
            assertThat(captors.capturedQuery().page, `is`(page))
        else if (like != null)
            assertThat(captors.capturedQuery().like, `is`(like))
        else if (sort != null)
            assertThat(captors.capturedQuery().sort, `is`(sort))
        return this
    }

    override fun currentState(): State? {
        return viewModel.noteList.value?.status
    }
}