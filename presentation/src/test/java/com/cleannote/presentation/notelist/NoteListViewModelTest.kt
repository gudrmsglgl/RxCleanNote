package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.BaseViewModelTest
import com.cleannote.presentation.Complete
import com.cleannote.presentation.OnError
import com.cleannote.presentation.OnSuccess
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.test.InstantExecutorExtension
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class NoteListViewModelTest: BaseViewModelTest() {

    lateinit var insertNewNote: InsertNewNote

    lateinit var searchNotes: SearchNotes

    lateinit var sharedPreferences: SharedPreferences

    private lateinit var noteListOnSuccessCaptor: KArgumentCaptor<OnSuccess<List<Note>>>
    private lateinit var queryCaptor: KArgumentCaptor<Query>

    private lateinit var insertNoteOnSuccessCaptor: KArgumentCaptor<OnSuccess<Long>>
    private lateinit var insertNoteParam: KArgumentCaptor<Note>

    private lateinit var onErrorCaptor: KArgumentCaptor<OnError>
    private lateinit var afterFinishedCaptor: KArgumentCaptor<Complete>
    private lateinit var onCompleteCaptor: KArgumentCaptor<Complete>

    private lateinit var noteListViewModel: NoteListViewModel

    @BeforeEach
    fun setUp(){

        viewModelState = MutableLiveData()
        viewModelState.observeForever(stateObserver)

        initMock()
        initNoteListCaptor()
        initInsertNoteCaptor()
        initCommonCaptor()

        sharedPreferences = mock{
            on { getString(FILTER_ORDERING_KEY, ORDER_DESC) } doReturn ORDER_DESC
        }

        noteListViewModel = NoteListViewModel(
            searchNotes, insertNewNote, noteMapper, sharedPreferences)
    }

    @AfterEach
    fun release(){
        viewModelState.removeObserver(stateObserver)
    }

    @Test
    fun searchNotesExecuteUseCase(){
        noteListViewModel.searchNotes()
        verify(sharedPreferences, times(1)).getString(FILTER_ORDERING_KEY, ORDER_DESC)
        verifySearchNoteExecute()
    }

    @Test
    fun searchNotesStateLoadingReturnNoData(){
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        verifyViewModelDataState(LOADING)
        assertViewModelNotesEqual(null)
    }

    @Test
    fun searchNotesStateLoadingToSuccessReturnData(){
        val noteList = NoteFactory.createNoteList(0, 10)
        val noteViewList = NoteFactory.createNoteViewList(0,10)
        noteViewList.forEachIndexed { index, noteView ->
            noteList[index] stubTo noteView
        }
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        verifyViewModelDataState(LOADING)

        whenSuccessOnNextNoteList(noteList)
        verifyViewModelDataState(SUCCESS)
        assertViewModelNotesEqual(noteViewList)
    }

    @Test
    fun searchNotesStateErrorReturnErrorMessageNoData(){
        val errorMessage = "stubErrorMessage"
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        verifyViewModelDataState(LOADING)

        whenErrorOnNextNoteList(errorMessage)
        verifyViewModelDataState(ERROR)

        assertViewModelNotesMessage(errorMessage)
        assertViewModelNotesEqual(null)
    }

    @Test
    fun searchNoteNextPageReturnDataTotalSize_20(){
        val p1NoteList = NoteFactory.createNoteList(0, 10)
        val p2NoteList = NoteFactory.createNoteList(10, 20)
        val noteViewList = NoteFactory.createNoteViewList(0,20)
        noteViewList.forEachIndexed { index, noteView ->
            if (index < 10)
                p1NoteList[index] stubTo noteView
            else
                p2NoteList[index-10] stubTo noteView
        }

        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        whenSuccessOnNextNoteList(p1NoteList)
        assertViewModelNotesSize(p1NoteList.size)

        whenNextSearchNotesSaveState()
        assertThat(queryCaptor.firstValue.page, `is`(2))
        whenSuccessOnNextNoteList(p2NoteList)
        verifyViewModelDataState(LOADING, times(2))
        verifyViewModelDataState(SUCCESS, times(2))
        assertViewModelNotesSize(p1NoteList.size + p2NoteList.size)
    }

    @Test
    fun searchNoteOrderingDESC(){
        noteListViewModel.setOrdering(ORDER_DESC)
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        assertThat(queryCaptor.firstValue.order, `is`(ORDER_DESC))
    }

    @Test
    fun searchNoteOrderingASC(){
        noteListViewModel.setOrdering(ORDER_ASC)
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        assertThat(queryCaptor.firstValue.order, `is`(ORDER_ASC))
    }

    @Test
    fun searchNoteKeyWordQuery(){
        val keyword = "TestQuery"
        noteListViewModel.searchKeyword(keyword)
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        assertThat(queryCaptor.firstValue.like, `is`(keyword))
    }

    @Test
    fun searchNoteAfterKeyWordQuery_ReturnOnlyKeywordData(){
        val keyword = "TestQuery"
        val nonKeyWordNoteList = NoteFactory.createNoteList(0,20)
        val keyWordNoteList = NoteFactory.createNoteList(0,5)
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        whenSuccessOnNextNoteList(nonKeyWordNoteList)
        assertViewModelNotesSize(nonKeyWordNoteList.size)

        noteListViewModel.searchKeyword(keyword)
        whenSearchNoteSaveState()
        whenSuccessOnNextNoteList(keyWordNoteList)
        assertViewModelNotesSize(keyWordNoteList.size)
    }

    @Test
    fun insertNotesExecuteUseCase(){
        val insertedNoteView = givenStubInsertNote()
        whenInsertNoteSaveState(insertedNoteView)
        verifyInsertNoteExecute()
    }

    @Test
    fun insertNotesLoadingNoData(){
        val insertedNoteView = givenStubInsertNote()
        whenInsertNoteSaveState(insertedNoteView)
        verifyInsertNoteExecute()
        verifyViewModelDataState(LOADING)
        assertViewModelInsertNoteEqual(null)
    }

    @Test
    fun insertNotesSuccessReturnNoteView(){
        val insertedNoteView = givenStubInsertNote()
        whenInsertNoteSaveState(insertedNoteView)
        verifyInsertNoteExecute()
        verifyViewModelDataState(LOADING)
        whenInsertedSuccessSaveState()
        verifyViewModelDataState(SUCCESS)
        assertViewModelInsertNoteEqual(insertedNoteView)
    }

    @Test
    fun insertNoteErrorReturnErrorMessageNoData(){
        val errorMessage = "TestError"
        val insertedNoteView = givenStubInsertNote()
        whenInsertNoteSaveState(insertedNoteView)
        verifyInsertNoteExecute()
        verifyViewModelDataState(LOADING)

        whenErrorOnNextInsertNote(errorMessage)
        verifyViewModelDataState(ERROR)
        assertViewModelInsertMessage(errorMessage)
        assertViewModelInsertNoteEqual(null)
    }

    @Test
    fun updateNoteSuccessThenSortingTop(){
        val noteList = NoteFactory.createNoteList(0, 10)
        val noteViewList = NoteFactory.createNoteViewList(0,10)
        noteViewList.forEachIndexed { index, noteView ->
            noteList[index] stubTo noteView
        }
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        verifyViewModelDataState(LOADING)

        whenSuccessOnNextNoteList(noteList)
        verifyViewModelDataState(SUCCESS)
        assertViewModelNotesEqual(noteViewList)

        val updateIndex = 2
        val updateTitle = "updateTitle"
        val updateNoteView = NoteFactory.oneOfNotesUpdate(noteViewList, updateIndex, updateTitle, null)
        whenUpdateNote(updateNoteView)

        checkViewModelNotesUpdate(index = 0, updateNoteView = updateNoteView)
    }

    @Test
    fun equalNoteNotUpdate(){
        val noteList = NoteFactory.createNoteList(0, 10)
        val noteViewList = NoteFactory.createNoteViewList(0,10)
        noteViewList.forEachIndexed { index, noteView ->
            noteList[index] stubTo noteView
        }
        whenSearchNoteSaveState()
        verifySearchNoteExecute()
        verifyViewModelDataState(LOADING)

        whenSuccessOnNextNoteList(noteList)
        verifyViewModelDataState(SUCCESS)
        assertViewModelNotesEqual(noteViewList)

        val updateIndex = 2
        val updateNoteView = noteViewList[updateIndex]
        whenUpdateNote(updateNoteView)

        assertViewModelNotesEqual(noteViewList) // when update then not sorting b/c equal note don't execute update
    }

    private fun whenUpdateNote(updateNote: NoteView){
        noteListViewModel.updateNote(updateNote)
        setViewModelState(noteListViewModel.noteList.value?.status)
    }

    private fun checkViewModelNotesUpdate(index: Int, updateNoteView: NoteView){
        assertThat(
            noteListViewModel.noteList.value?.data?.get(index),
            `is`(updateNoteView)
        )
    }

    private fun whenSearchNoteSaveState(){
        noteListViewModel.searchNotes()
        setViewModelState(noteListViewModel.noteList.value?.status)
    }

    private fun whenNextSearchNotesSaveState(){
        with(noteListViewModel){
            nextPage()
            searchNotes()
        }
        setViewModelState(noteListViewModel.noteList.value?.status)
    }

    private fun whenSuccessOnNextNoteList(noteList: List<Note>){
        noteListOnSuccessCaptor.firstValue.invoke(noteList)
        setViewModelState(noteListViewModel.noteList.value?.status)
    }

    private fun whenErrorOnNextNoteList(message: String) {
        onErrorCaptor.firstValue.invoke(RuntimeException(message))
        setViewModelState(noteListViewModel.noteList.value?.status)
    }

    private fun verifyInsertNoteExecute(){
        insertNewNote.verifyExecute(insertNoteOnSuccessCaptor, onErrorCaptor, afterFinishedCaptor, onCompleteCaptor, insertNoteParam)
    }

    private fun givenStubInsertNote(): NoteView{
        val insertedNoteView = NoteFactory.createNoteView(title="insertTitle", date = "1")
        whenever(noteMapper.mapFromView(insertedNoteView)).thenReturn(NoteFactory.createNote(title="insertTitle", date = "1"))
        return insertedNoteView
    }

    private fun whenInsertNoteSaveState(insertNoteView: NoteView){
        noteListViewModel.insertNotes(insertNoteView)
        setViewModelState(noteListViewModel.insertResult.value?.status)
    }

    private fun whenInsertedSuccessSaveState(){
        insertNoteOnSuccessCaptor.firstValue.invoke(1L)
        setViewModelState(noteListViewModel.insertResult.value?.status)
    }

    private fun whenErrorOnNextInsertNote(message: String){
        onErrorCaptor.firstValue.invoke(RuntimeException(message))
        setViewModelState(noteListViewModel.insertResult.value?.status)
    }

    private fun assertViewModelNotesEqual(expectedData: List<NoteView>?){
        if (expectedData == null) {
            assertThat(noteListViewModel.noteList.value?.data, `is`(nullValue()))
        } else {
            assertThat(
                noteListViewModel.noteList.value?.data,
                `is`(expectedData)
            )
        }
    }

    private fun assertViewModelInsertNoteEqual(expectedData: NoteView?){
        if (expectedData == null) {
            assertThat(noteListViewModel.insertResult.value?.data, `is`(nullValue()))
        } else {
            assertThat(
                noteListViewModel.insertResult.value?.data,
                `is`(expectedData)
            )
        }
    }

    private fun assertViewModelNotesSize(expectedSize: Int){
        assertThat(noteListViewModel.noteList.value?.data?.size, `is`(expectedSize))
    }

    private fun assertViewModelNotesMessage(message: String) {
        assertThat(noteListViewModel.noteList.value?.message, `is`(message))
    }

    private fun assertViewModelInsertMessage(message: String) {
        assertThat(noteListViewModel.insertResult.value?.message, `is`(message))
    }

    private fun verifySearchNoteExecute(){
        searchNotes.verifyExecute(noteListOnSuccessCaptor, onErrorCaptor, afterFinishedCaptor, onCompleteCaptor, queryCaptor)
    }

    private fun initNoteListCaptor(){
        noteListOnSuccessCaptor = argumentCaptor()
        queryCaptor = argumentCaptor()
    }

    private fun initInsertNoteCaptor(){
        insertNoteOnSuccessCaptor = argumentCaptor()
        insertNoteParam = argumentCaptor()
    }

    private fun initCommonCaptor(){
        onErrorCaptor = argumentCaptor()
        afterFinishedCaptor = argumentCaptor()
        onCompleteCaptor = argumentCaptor()
    }

    private fun initMock(){
        insertNewNote = mock()
        searchNotes = mock()
        noteMapper = mock()
    }
}