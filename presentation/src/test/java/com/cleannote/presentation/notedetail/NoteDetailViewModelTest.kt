package com.cleannote.presentation.notedetail

import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.BaseViewModelTest
import com.cleannote.presentation.Complete
import com.cleannote.presentation.OnError
import com.cleannote.presentation.OnSuccess
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.LOADING
import com.cleannote.presentation.data.State.SUCCESS
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

    private lateinit var onSuccessNoteCaptor: KArgumentCaptor<OnSuccess<Unit>>
    private lateinit var onErrorNoteCaptor: KArgumentCaptor<OnError>
    private lateinit var afterFinishedCaptor: KArgumentCaptor<Complete>
    private lateinit var onCompleteCaptor: KArgumentCaptor<Complete>

    private lateinit var updateParamCaptor: KArgumentCaptor<Note>

    private val note = NoteFactory.createNote("testNote")
    private val noteView = NoteFactory.createNoteView("testNote")

    @BeforeEach
    fun setUp(){
        updateNote = mock()
        noteMapper = mock()

        onSuccessNoteCaptor = argumentCaptor()
        onErrorNoteCaptor = argumentCaptor()
        afterFinishedCaptor = argumentCaptor()
        onCompleteCaptor = argumentCaptor()

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
        verifyUpdateNoteExecute()
    }

    @Test
    fun updateNoteStateLoadingReturnNoData(){
        stubNoteMapper(noteView, note)
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)
        verifyUpdateViewModelData(null)
    }

    @Test
    fun updateNoteStateSuccessReturnNote(){
        stubNoteMapper(noteView, note)
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)

        onSuccessUpdateNote()
        verifyViewModelDataState(SUCCESS)
        verifyUpdateViewModelData(noteView)
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

    private fun verifyUpdateNoteExecute(){
        updateNote.verifyExecute(
            onSuccessNoteCaptor,
            onErrorNoteCaptor,
            afterFinishedCaptor,
            onCompleteCaptor,
            updateParamCaptor
        )
    }

    private fun onSuccessUpdateNote(){
        onSuccessNoteCaptor.firstValue.invoke(Unit)
        setViewModelState(viewModel.updatedNote.value?.status)
    }
}