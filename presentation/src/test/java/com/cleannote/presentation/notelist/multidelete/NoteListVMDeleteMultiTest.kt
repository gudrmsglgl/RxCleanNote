package com.cleannote.presentation.notelist.multidelete

import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.extensions.transNoteViews
import com.cleannote.presentation.extensions.transNotes
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NewNoteListViewModelTest
import com.cleannote.presentation.notelist.multidelete.tester.DeleteMultiUseCaseCaptors
import com.cleannote.presentation.notelist.multidelete.tester.DeleteMultipleFeatureTester
import com.cleannote.presentation.notelist.search.tester.SearchFeatureTester
import com.cleannote.presentation.notelist.search.tester.SearchUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteListVMDeleteMultiTest: NewNoteListViewModelTest() {

    private lateinit var deleteMultiUseCaseCaptors: DeleteMultiUseCaseCaptors
    private lateinit var deleteMultipleFeatureTester: DeleteMultipleFeatureTester

    private lateinit var captors: SearchUseCaseCaptors
    private lateinit var searchFeatureTester: SearchFeatureTester

    @BeforeEach
    fun deleteMultiSetup(){
        deleteMultiUseCaseCaptors = DeleteMultiUseCaseCaptors()
        deleteMultipleFeatureTester = DeleteMultipleFeatureTester(noteListViewModel, useCases.deleteMultipleNotes, deleteMultiUseCaseCaptors)

        captors = SearchUseCaseCaptors()
        searchFeatureTester = SearchFeatureTester(noteListViewModel, useCases.searchNotes, captors)
    }

    @Test
    fun deleteMultipleNotesExecuteUseCase(){
        val deleteIndex = 0
        val deleteIndex2 = 2

        val noteViews = NoteFactory.createNoteViewList(0,5)
        val selectedNotes = listOf(noteViews[deleteIndex], noteViews[deleteIndex2])

        with(deleteMultipleFeatureTester){
            deleteMultiNotes(selectedNotes)
                .verifyUseCaseExecute()
        }

    }

    @Test
    fun deleteMultipleNotesStateSuccessReturnDeletedNotes(){
        val noteViewList = NoteFactory.createNoteViewList(0, 10).asReversed().toMutableList()

        with(searchFeatureTester){
            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(noteViewList.transNotes())
                .expectData(noteViewList)
        }

        val selectDeleteNotes = listOf(noteViewList[0], noteViewList[2])
        val totalNotesAfterDeleted = noteViewList.apply {
            selectDeleteNotes.forEach { remove(it) }
        }

        with (deleteMultipleFeatureTester) {
            deleteMultiNotes(selectDeleteNotes)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnComplete()
                .verifyChangeState(SUCCESS)
                .expectTotalNote(totalNotesAfterDeleted)
        }
    }

    @Test
    fun deleteMultipleNotesStateErrorReturnThrowableNotDeletedNotes(){
        val noteViewList = NoteFactory.createNoteViewList(0, 10).asReversed().toMutableList()

        with(searchFeatureTester){
            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(noteViewList.transNotes())
                .expectData(noteViewList)
        }

        val throwable = RuntimeException()
        val selectDeleteNotes = listOf(noteViewList[0], noteViewList[2])

        with (deleteMultipleFeatureTester) {
            deleteMultiNotes(selectDeleteNotes)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnError(throwable)
                .verifyChangeState(ERROR)
                .expectTotalNote(noteViewList)
        }
    }

}