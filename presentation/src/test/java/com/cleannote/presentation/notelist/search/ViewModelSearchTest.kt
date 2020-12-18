package com.cleannote.presentation.notelist.search

import com.cleannote.domain.Constants
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.notelist.NewNoteListViewModelTest
import com.cleannote.presentation.test.InstantExecutorExtension
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class ViewModelSearchTest: NewNoteListViewModelTest() {

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
        val noteList = NoteFactory.createNoteList(0, 10)

        with(searchFeatureTester) {

            search()
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnSuccess(noteList)
                .verifyChangeState(SUCCESS)
                .expectData(noteList)
        }
    }

    @Test
    fun searchNotesStateErrorReturnErrorMessageNoData(){
        val throwable = RuntimeException()
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
        val noteList = NoteFactory.createNoteList(0, 20)
        with(searchFeatureTester) {

            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(noteList.subList(0, 10))
                .expectQuery(page = 1)
                .expectData(noteList.subList(0, 10))


            search(isNextPage = true)
                .stubUseCaseOnSuccess(noteList.subList(10, 20))
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
        val nonKeyWordNoteList = NoteFactory.createNoteList(0,10)
        val keyWordNoteList = NoteFactory.createNoteList(0,5)

        with(searchFeatureTester){

            search()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(nonKeyWordNoteList)
                .expectData(nonKeyWordNoteList)

            searchKeyword(keyword)
                .search()
                .stubUseCaseOnSuccess(keyWordNoteList)
                .expectQuery(like = keyword)
                .expectData(keyWordNoteList)

        }
    }

}