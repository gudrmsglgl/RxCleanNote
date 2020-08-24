package com.cleannote.presentation.notedetail

import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.BaseViewModelTest
import com.cleannote.presentation.data.State.LOADING
import com.cleannote.presentation.data.notedetail.TextMode.EditDoneMode
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.test.InstantExecutorExtension
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.subscribers.DisposableSubscriber
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class NoteDetailViewModelTest: BaseViewModelTest() {

    private lateinit var viewModel: NoteDetailViewModel

    private lateinit var updateNote: UpdateNote
    private lateinit var noteMapper: NoteMapper

    private lateinit var updateNoteCaptor: KArgumentCaptor<DisposableSubscriber<Unit>>
    private lateinit var updateParamCaptor: KArgumentCaptor<Note>

    private val note = NoteFactory.createNote("testNote")
    private val noteView = NoteFactory.createNoteView("testNote")

    @BeforeEach
    fun setUp(){
        updateNote = mock()
        noteMapper = mock()

        updateNoteCaptor = argumentCaptor()
        updateParamCaptor = argumentCaptor()

        viewModelState = MutableLiveData()
        viewModelState.observeForever(stateObserver)

        viewModel = NoteDetailViewModel(updateNote, noteMapper)
    }

    @AfterEach
    fun release(){
        viewModelState.removeObserver(stateObserver)
    }

    @Test
    fun updateNoteExecuteUseCase(){
        stubNoteMapper(noteView, note)
        whenUpdateNote(noteView)
        verifyUseCaseExecute(updateNote, updateNoteCaptor, updateParamCaptor)
    }

    @Test
    fun updateNoteStateLoadingReturnNoData(){
        stubNoteMapper(noteView, note)
        whenUpdateNote(noteView)
        verifyUseCaseExecute(updateNote, updateNoteCaptor, updateParamCaptor)
        verifyViewModelDataState(LOADING)
        verifyUpdateViewModelData(null)
    }

    private fun stubNoteMapper(noteView: NoteView, note: Note){
        whenever(noteMapper.mapFromView(noteView)).thenReturn(note)
    }

    private fun whenUpdateNote(noteView: NoteView){
        with(viewModel) {
            setNote(noteView)
            setNoteMode(EditDoneMode)
        }
        setViewModelState(viewModel.updatedNote.value?.status)
    }

    private fun verifyUpdateViewModelData(expectedData: NoteView?){
        if (expectedData == null) {
            assertThat(viewModel.updatedNote.value?.data, `is`(nullValue()))
        } else {
            assertThat(
                viewModel.updatedNote.value?.data,
                `is`(expectedData)
            )
        }
    }

}