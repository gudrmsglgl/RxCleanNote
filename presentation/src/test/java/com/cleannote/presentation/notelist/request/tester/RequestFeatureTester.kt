package com.cleannote.presentation.notelist.request.tester

import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.presentation.notelist.search.tester.SearchFeatureTester
import com.cleannote.presentation.notelist.search.tester.SearchUseCaseCaptors
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat

class RequestFeatureTester(
    private val vm: NoteListViewModel,
    private val usecase: SearchNotes,
    private val searchUseCaseCaptors: SearchUseCaseCaptors
) : SearchFeatureTester(vm, usecase, searchUseCaseCaptors) {

    fun requestUpdate(param: NoteView): RequestFeatureTester {
        vm.reqUpdateFromDetailFragment(param)
        return this
    }

    fun expectFirstNote(param: NoteView): RequestFeatureTester {
        assertThat(vmCurrentData()!!.data!![0], `is`(param))
        return this
    }

    fun requestDelete(param: NoteView): RequestFeatureTester {
        vm.reqDeleteFromDetailFragment(param)
        return this
    }

    fun hasNoData(param: NoteView): RequestFeatureTester {
        assertThat(vmCurrentData()!!.data, not(hasItem(param)))
        return this
    }
}
