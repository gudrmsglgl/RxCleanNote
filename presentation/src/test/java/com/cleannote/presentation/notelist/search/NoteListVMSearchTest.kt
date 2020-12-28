package com.cleannote.presentation.notelist.search

import com.cleannote.domain.Constants
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.extensions.transNotes
import com.cleannote.presentation.notelist.NoteListViewModelTest
import com.cleannote.presentation.notelist.search.tester.SearchFeatureTester
import com.cleannote.presentation.notelist.search.tester.SearchUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteListVMSearchTest: NoteListViewModelTest() {

    private lateinit var captors: SearchUseCaseCaptors
    private lateinit var searchFeatureTester: SearchFeatureTester

    @BeforeEach
    fun searchTestSetup() {
        captors = SearchUseCaseCaptors()
        searchFeatureTester = SearchFeatureTester(noteListViewModel, searchNotes, captors)
    }

    @Test
    fun searchNotesExecuteUseCase(){
        with(searchFeatureTester){
            search()
                .verifyUseCaseExecute()
        }
        verify(sharedPreferences, times(1)).getString(
            Constants.FILTER_ORDERING_KEY,
            Constants.ORDER_DESC
        )
    }

    @Test
    fun searchNotesStateLoadingReturnNoData(){
        with(searchFeatureTester){
            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)
                .expectData(null)
        }
    }

    @Test
    fun searchNotesStateLoadingToSuccessReturnData(){
        val notes = NoteFactory.createNoteViewList(0, 10)
        with(searchFeatureTester) {

            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnSuccess(notes.transNotes())
                .verifyChangeState(SUCCESS)
                .expectData(notes)
        }
    }

    @Test
    fun searchNotesStateErrorReturnThrowableNoData(){
        val errorMessage = "RunTimeException Test"
        val throwable = RuntimeException(errorMessage)
        with(searchFeatureTester){

            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnError(throwable)
                .verifyChangeState(ERROR)
                .expectData(null)
                .expectError(throwable)

        }
    }
    @Test
    fun searchNoteNextPageReturnDataTotalSize_20(){
        val noteList = NoteFactory.createNoteViewList(0, 20).asReversed()
        with(searchFeatureTester) {

            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(noteList.subList(0, 10).transNotes())
                .expectQuery(page = 1)
                .expectData(noteList.subList(0, 10))


            search(isNextPage = true)
                .stubUseCaseOnSuccess(noteList.subList(10, 20).transNotes())
                .expectQuery(page = 2)
                .expectData(noteList)

            verifyChangeState(LOADING, times(2))
            verifyChangeState(SUCCESS, times(2))

        }
    }

    @Test
    fun searchNoteDefaultOrderingDESC(){
        with(searchFeatureTester){
            search()
                .verifyUseCaseExecute()
                .expectQuery(order = ORDER_DESC)
        }
    }

    @Test
    fun searchNoteSetOrderingASC(){
        with(searchFeatureTester){
            setOrdering(ORDER_ASC)
            search()
                .verifyUseCaseExecute()
                .expectQuery(order = ORDER_ASC)
        }
    }

    @Test
    fun searchNoteKeyWordQuery(){
        val keyword = "TestQuery"
        with(searchFeatureTester){
            searchKeyword(keyword)
            search()
                .verifyUseCaseExecute()
                .expectQuery(like = keyword)
        }
    }

    @Test
    fun searchNoteAfterKeyWordQuery_ReturnOnlyKeywordData(){
        val keyword = "TestQuery"
        val defaultNoteList = NoteFactory.createNoteViewList(0,10)
        val keyWordNoteList = NoteFactory.createNoteViewList(0,5)

        with(searchFeatureTester){

            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(defaultNoteList.transNotes())
                .expectData(defaultNoteList)

            searchKeyword(keyword)
                .search()
                .stubUseCaseOnSuccess(keyWordNoteList.transNotes())
                .expectQuery(like = keyword)
                .expectData(keyWordNoteList)

        }
    }

}