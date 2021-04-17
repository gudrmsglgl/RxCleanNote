package com.cleannote.presentation.notelist.request

import com.cleannote.presentation.data.State.LOADING
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.extensions.transNotes
import com.cleannote.presentation.notelist.NoteListViewModelTest
import com.cleannote.presentation.notelist.request.tester.RequestFeatureTester
import com.cleannote.presentation.notelist.search.tester.SearchUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteListVMRequestTest : NoteListViewModelTest() {

    private lateinit var captors: SearchUseCaseCaptors
    private lateinit var requestFeatureTester: RequestFeatureTester

    @BeforeEach
    fun requestTestSetup() {
        captors = SearchUseCaseCaptors()
        requestFeatureTester = RequestFeatureTester(noteListViewModel, searchNotes, captors)
    }

    @Test
    fun reqUpdateFromDetailFragmentSuccessThenSortingTop() {
        val noteViews = NoteFactory.createNoteViewList(0, 10)
        val updateNoteView = NoteFactory.oneOfNotesUpdate(notes = noteViews, index = 2, title = "updateTitle", body = null)
        with(requestFeatureTester) {
            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnSuccess(noteViews.transNotes())
                .verifyChangeState(SUCCESS)
                .expectData(noteViews)

            requestUpdate(updateNoteView)
                .expectFirstNote(updateNoteView)
                .verifyChangeState(SUCCESS)
        }
    }

    @Test
    fun reqDeleteFromDetailFragmentSuccessThenHasNoData() {
        val noteViews = NoteFactory.createNoteViewList(0, 10).toMutableList()
        val reqDeletedNote = noteViews[2]
        with(requestFeatureTester) {
            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnSuccess(noteViews.transNotes())
                .verifyChangeState(SUCCESS)
                .expectData(noteViews)

            requestDelete(reqDeletedNote)
                .hasNoData(reqDeletedNote)
                .verifyChangeState(SUCCESS)
                .expectData(noteViews.apply { remove(reqDeletedNote) })
        }
    }
}
