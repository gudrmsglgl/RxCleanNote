package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.data.State
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.test.InstantExecutorExtension
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.subscribers.DisposableSubscriber
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.RuntimeException

@ExtendWith(InstantExecutorExtension::class)
class NoteListViewModelTest {

    lateinit var getNumNotes: GetNumNotes
    lateinit var insertNewNote: InsertNewNote
    lateinit var searchNotes: SearchNotes
    lateinit var noteMapper: NoteMapper
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var noteCaptor: KArgumentCaptor<DisposableSubscriber<List<Note>>>
    private lateinit var queryCaptor: KArgumentCaptor<Query>
    private lateinit var liveState: MutableLiveData<State>
    private lateinit var stateObserver: Observer<State>

    private lateinit var noteListViewModel: NoteListViewModel


    @BeforeEach
    fun setUp(){
        liveState = MutableLiveData()
        stateObserver = mock()
        liveState.observeForever(stateObserver)
        noteCaptor = argumentCaptor()
        queryCaptor = argumentCaptor()
        insertNewNote = mock()
        getNumNotes = mock()
        searchNotes = mock()
        noteMapper = mock()
        sharedPreferences = mock{
            on { getString(FILTER_ORDERING_KEY, ORDER_DESC) } doReturn ORDER_DESC
        }
        noteListViewModel = NoteListViewModel(
            getNumNotes, searchNotes, insertNewNote, noteMapper, sharedPreferences)
    }

    @AfterEach
    fun release(){
        liveState.removeObserver(stateObserver)
    }

    @Test
    fun searchNotesExecuteUseCase(){
        noteListViewModel.searchNotes()
        verify(sharedPreferences, times(1)).getString(FILTER_ORDERING_KEY, ORDER_DESC)
        verify(searchNotes).execute(any(), anyOrNull())
    }

    @Test
    fun searchNotesStateLoadingReturnNoData(){
        whenSearchNoteSaveState()

        verify(searchNotes).execute(any(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)
        assertThat(noteListViewModel.noteList.value?.data, `is`(nullValue()))
    }

    @Test
    fun searchNotesStateLoadingToSuccessReturnData(){
        val noteList = NoteFactory.createNoteList(0, 10)
        val noteViewList = NoteFactory.createNoteViewList(0,10)
        noteViewList.forEachIndexed { index, noteView ->
            whenever(noteMapper.mapToView(noteList[index])).thenReturn(noteView)
        }

        whenSearchNoteSaveState()

        verifySearchNoteUseCase()
        verify(stateObserver).onChanged(State.LOADING)

        whenOnNextNoteSaveState(noteList)

        verify(stateObserver).onChanged(State.SUCCESS)
        assertThat(noteListViewModel.noteList.value?.data, `is`(noteViewList))
    }

    @Test
    fun searchNotesStateErrorReturnErrorMessage(){
        val errorMessage: String = "stubErrorMessage"

        whenSearchNoteSaveState()

        verifySearchNoteUseCase()
        verify(stateObserver).onChanged(State.LOADING)

        noteCaptor.firstValue.onError(RuntimeException(errorMessage))
        setCurrentState(noteListViewModel.noteList.value?.status)

        verify(stateObserver).onChanged(State.ERROR)
        assertThat(errorMessage, `is`(noteListViewModel.noteList.value?.message))
    }

    @Test
    fun searchNoteNextPageReturnDataTotalSize_20(){
        val p1NoteList = NoteFactory.createNoteList(0, 10)
        val p2NoteList = NoteFactory.createNoteList(10, 20)
        val noteViewList = NoteFactory.createNoteViewList(0,20)

        noteViewList.forEachIndexed { index, noteView ->
            if (index < 10)
                whenever(noteMapper.mapToView(p1NoteList[index])).thenReturn(noteView)
            else
                whenever(noteMapper.mapToView(p2NoteList[index-10])).thenReturn(noteView)
        }

        whenSearchNoteSaveState()

        verifySearchNoteUseCase()

        whenOnNextNoteSaveState(p1NoteList)
        assertThat(noteListViewModel.noteList.value?.data?.size, `is`(10))

        whenNextSearchNotesSaveState()
        assertThat(queryCaptor.firstValue.page, `is`(2))

        whenOnNextNoteSaveState(p2NoteList)

        verify(stateObserver, times(2)).onChanged(State.LOADING)
        verify(stateObserver, times(2)).onChanged(State.SUCCESS)
        assertThat(noteListViewModel.noteList.value?.data?.size, `is`(20))
    }

    @Test
    fun insertNotesExecuteUseCase(){
        val insertedNoteView = NoteFactory.createNoteView("#1")
        whenever(noteMapper.mapFromView(insertedNoteView)).thenReturn(NoteFactory.createNote("#1"))

        noteListViewModel.insertNotes(insertedNoteView)

        verify(insertNewNote).execute(any(), anyOrNull())
    }


    private fun setCurrentState(state: State?){
        liveState.value = state
    }

    private fun stubNoteMapperMapToView(noteView: NoteView, note: Note){
        whenever(noteMapper.mapToView(note)).thenReturn(noteView)
    }

    private fun verifySearchNoteUseCase(){
        verify(searchNotes).execute(noteCaptor.capture(), queryCaptor.capture())
    }

    private fun whenSearchNoteSaveState(){
        noteListViewModel.searchNotes()
        setCurrentState(noteListViewModel.noteList.value?.status)
    }

    private fun whenNextSearchNotesSaveState(){
        with(noteListViewModel){
            nextPage()
            searchNotes()
        }
        setCurrentState(noteListViewModel.noteList.value?.status)
    }

    private fun whenOnNextNoteSaveState(noteList: List<Note>){
        noteCaptor.firstValue.onNext(noteList)
        setCurrentState(noteListViewModel.noteList.value?.status)
    }
}