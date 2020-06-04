package com.cleannote.presentation.notelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.NoteListInteractors
import com.cleannote.domain.model.Note
import com.cleannote.presentation.data.DataState
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
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Spy

@ExtendWith(InstantExecutorExtension::class)
class NoteListViewModelTest {

    lateinit var getNumNotes: GetNumNotes
    lateinit var noteMapper: NoteMapper
    private lateinit var captor: KArgumentCaptor<DisposableSubscriber<List<Note>>>
    private lateinit var liveState: MutableLiveData<State>
    private lateinit var stateObserver: Observer<State>

    private lateinit var noteListViewModel: NoteListViewModel


    @BeforeEach
    fun setUp(){
        liveState = MutableLiveData()
        stateObserver = mock()
        liveState.observeForever(stateObserver)
        captor = argumentCaptor()
        getNumNotes = mock()
        noteMapper = mock()
        noteListViewModel = NoteListViewModel(getNumNotes, noteMapper)
    }

    @AfterEach
    fun release(){
        liveState.removeObserver(stateObserver)
    }

    @Test
    fun getNumNotesExecuteUseCase(){
        noteListViewModel.noteList
        verify(getNumNotes).execute(any(), anyOrNull())
    }

    @Test
    fun getNumNotesReturnLoading() {

        setCurrentState(noteListViewModel.noteList.value?.status)

        verify(getNumNotes).execute(captor.capture(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)

        assertThat(
            noteListViewModel.noteList.value?.status,
            instanceOf(State.LOADING::class.java)
        )
    }

    @Test
    fun getNumNotesReturnLoadingNoData(){
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(getNumNotes).execute(any(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)
        assertThat(noteListViewModel.noteList.value?.data, `is`(nullValue()))
    }

    @Test
    fun getNumNotesReturnLoadingNoMessage(){
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(getNumNotes).execute(any(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)
        assertThat(noteListViewModel.noteList.value?.message, `is`(nullValue()))
    }

    @Test
    fun getNumNotesReturnLoadingToSuccess(){
        val noteList = NoteFactory.createNoteList(5)
        val noteViewList = NoteFactory.createNoteViewList(5)
        noteViewList.forEachIndexed { index, noteView ->
            stubNoteMapperMapToView(noteView, noteList[index])
        }
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onNext(noteList)

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.SUCCESS)
    }

    @Test
    fun getNumNotesReturnSuccessData(){
        val noteList = NoteFactory.createNoteList(5)
        val noteViewList = NoteFactory.createNoteViewList(5)
        noteViewList.forEachIndexed { index, noteView ->
            stubNoteMapperMapToView(noteView, noteList[index])
        }
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onNext(noteList)

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.SUCCESS)
        assertThat(noteListViewModel.noteList.value?.data, `is`(noteViewList))
    }

    @Test
    fun getNumNotesReturnSuccessNoMessage(){
        val noteList = NoteFactory.createNoteList(5)
        val noteViewList = NoteFactory.createNoteViewList(5)
        noteViewList.forEachIndexed { index, noteView ->
            stubNoteMapperMapToView(noteView, noteList[index])
        }
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onNext(noteList)

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.SUCCESS)
        assertThat(noteListViewModel.noteList.value?.message, `is`(nullValue()))
    }

    @Test
    fun getNumNotesReturnError(){
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onError(RuntimeException())

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.ERROR)
    }

    @Test
    fun getNumNotesReturnErrorNoData(){
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onError(RuntimeException())

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.ERROR)
        assertThat(noteListViewModel.noteList.value?.data, `is`(nullValue()))
    }

    @Test
    fun getNumNotesReturnErrorMessage(){
        val errorMessage = "RuntimeError"
        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.LOADING)

        verify(getNumNotes).execute(captor.capture(), eq(null))
        captor.firstValue.onError(RuntimeException(errorMessage))

        setCurrentState(noteListViewModel.noteList.value?.status)
        verify(stateObserver).onChanged(State.ERROR)
        assertThat(noteListViewModel.noteList.value?.message, `is`(errorMessage))
    }

    private fun setCurrentState(state: State?){
        liveState.value = state
    }

    private fun stubNoteMapperMapToView(noteView: NoteView, note: Note){
        whenever(noteMapper.mapToView(note)).thenReturn(noteView)
    }
}