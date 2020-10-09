package com.cleannote.presentation.notedetail

import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.BaseViewModelTest
import com.cleannote.presentation.Complete
import com.cleannote.presentation.OnError
import com.cleannote.presentation.OnSuccess
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.data.notedetail.TextMode.EditDoneMode
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
class NoteDetailViewModelTest: BaseViewModelTest() {

    private lateinit var viewModel: NoteDetailViewModel

    private lateinit var updateNote: UpdateNote
    private lateinit var deleteNote: DeleteNote

    private lateinit var onSuccessNoteCaptor: KArgumentCaptor<OnSuccess<Unit>>
    private lateinit var onErrorNoteCaptor: KArgumentCaptor<OnError>
    private lateinit var afterFinishedCaptor: KArgumentCaptor<Complete>
    private lateinit var onCompleteCaptor: KArgumentCaptor<Complete>

    private lateinit var noteParamCaptor: KArgumentCaptor<Note>

    private val noteView = NoteFactory.createNoteView(title = "testNote", date = "1")

    @BeforeEach
    fun setUp(){
        updateNote = mock()
        deleteNote = mock()

        onSuccessNoteCaptor = argumentCaptor()
        onErrorNoteCaptor = argumentCaptor()
        afterFinishedCaptor = argumentCaptor()
        onCompleteCaptor = argumentCaptor()

        noteParamCaptor = argumentCaptor()

        viewModelState = MutableLiveData()
        viewModelState.observeForever(stateObserver)

        viewModel = NoteDetailViewModel(updateNote, deleteNote)
    }

    @AfterEach
    fun release(){
        viewModelState.removeObserver(stateObserver)
    }

    @Test
    fun updateNoteExecuteUseCase(){
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
    }

    @Test
    fun updateNoteStateLoadingReturnNoData(){
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)
        assertViewModelUpdateNoteEqual(null)
    }

    @Test
    fun updateNoteStateSuccessReturnNote(){
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)

        onSuccessUpdateNote()
        verifyViewModelDataState(SUCCESS)
        assertViewModelUpdateNoteEqual(noteView)
    }

    @Test
    fun updateNoteStateErrorReturnThrowable(){
        val throwable = RuntimeException()
        whenUpdateNote(noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)

        onErrorUpdateNote(throwable)
        verifyViewModelDataState(ERROR)
        assertViewModelUpdateNoteHasThrowable(throwable)
    }

    @Test
    fun deleteNoteExecuteUseCase(){
        whenDeleteNote(noteView)
        verifyDeleteNoteExecute()
    }

    @Test
    fun deleteNoteStateLoadingNoData(){
        whenDeleteNote(noteView)
        verifyDeleteNoteExecute()
        verifyViewModelDataState(LOADING)
        assertViewModelDeleteNoteEqual(null)
    }

    @Test
    fun deleteNoteStateSuccessReturnNote(){
        whenDeleteNote(noteView)
        verifyDeleteNoteExecute()
        verifyViewModelDataState(LOADING)

        onSuccessDeleteNote()
        verifyViewModelDataState(SUCCESS)
        assertViewModelDeleteNoteEqual(noteView)
    }

    @Test
    fun deleteNoteStateErrorReturnThrowable(){
        val throwable = RuntimeException()
        whenDeleteNote(noteView)
        verifyDeleteNoteExecute()
        verifyViewModelDataState(LOADING)

        onErrorDeleteNote(throwable)
        verifyViewModelDataState(ERROR)
        assertViewModelDeleteNoteHasThrowable(throwable)
    }

    private fun whenUpdateNote(noteView: NoteView){
        with(viewModel) {
            setNote((noteView to EditDoneMode))
        }
        setViewModelState(viewModel.updatedNote.value?.status)
    }

    private fun onSuccessUpdateNote(){
        onCompleteCaptor.firstValue.invoke()
        setViewModelState(viewModel.updatedNote.value?.status)
    }

    private fun onErrorUpdateNote(throwable: Throwable){
        onErrorNoteCaptor.firstValue.invoke(throwable)
        setViewModelState(viewModel.updatedNote.value?.status)
    }


    private fun verifyUpdateNoteExecute(){
        updateNote.verifyExecute(
            onSuccessNoteCaptor,
            onErrorNoteCaptor,
            afterFinishedCaptor,
            onCompleteCaptor,
            noteParamCaptor
        )
    }

    private fun assertViewModelUpdateNoteEqual(expectedData: NoteView?){
        if (expectedData == null) {
            assertThat(viewModel.updatedNote.value?.data, `is`(nullValue()))
        } else {
            assertThat(
                viewModel.updatedNote.value?.data,
                `is`(expectedData)
            )
        }
    }

    private fun assertViewModelUpdateNoteHasThrowable(throwable: Throwable){
        assertThat(viewModel.updatedNote.value?.throwable, `is`(throwable))
    }

    private fun whenDeleteNote(noteView: NoteView){
        viewModel.deleteNote(noteView)
        setViewModelState(viewModel.deletedNote.value?.status)
    }

    private fun verifyDeleteNoteExecute(){
        deleteNote.verifyExecute(
            onSuccessNoteCaptor,
            onErrorNoteCaptor,
            afterFinishedCaptor,
            onCompleteCaptor,
            noteParamCaptor
        )
    }

    private fun assertViewModelDeleteNoteEqual(expectedData: NoteView?){
        if (expectedData == null) {
            assertThat(viewModel.deletedNote.value?.data, `is`(nullValue()))
        } else {
            assertThat(
                viewModel.deletedNote.value?.data,
                `is`(expectedData)
            )
        }
    }

    private fun assertViewModelDeleteNoteHasThrowable(throwable: Throwable){
        assertThat(viewModel.deletedNote.value?.throwable, `is`(throwable))
    }

    private fun onSuccessDeleteNote(){
        onCompleteCaptor.firstValue.invoke()
        setViewModelState(viewModel.deletedNote.value?.status)
    }

    private fun onErrorDeleteNote(throwable: Throwable){
        onErrorNoteCaptor.firstValue.invoke(throwable)
        setViewModelState(viewModel.deletedNote.value?.status)
    }
}