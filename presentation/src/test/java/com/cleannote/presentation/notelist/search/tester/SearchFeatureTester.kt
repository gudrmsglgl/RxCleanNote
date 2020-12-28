package com.cleannote.presentation.notelist.search.tester

import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

open class SearchFeatureTester(
    private val viewModel: NoteListViewModel,
    private val usecase: SearchNotes,
    private val searchCaptors: SearchUseCaseCaptors
): ViewModelFeatureTester<SearchFeatureTester, List<Note>, Query, List<NoteView>>(searchCaptors)
{
    override fun verifyUseCaseExecute(): SearchFeatureTester {
        usecase.verifyExecute(searchCaptors, searchCaptors.paramCaptor)
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

        val map = mapOf(
            order to searchCaptors.paramValue.order,
            page to searchCaptors.paramValue.page,
            like to searchCaptors.paramValue.like,
            sort to searchCaptors.paramValue.sort
        )

        map.filter {
            it.key != null
        }.forEach { (expect, actual) ->
            if (expect is Int && actual is Int) assertPage(actual, expect)
            else if (expect is String && actual is String?) assertQuery(actual, expect)
        }

        return this
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }

    override fun vmCurrentData(): DataState<List<NoteView>>? {
        return viewModel.noteList.value
    }

    private fun assertPage(actualPage: Int, expectPage: Int){
        assertThat(actualPage, `is`(expectPage))
    }

    private fun assertQuery(actual: String?, expect: String){
        assertThat(actual, `is`(expect))

    }

}