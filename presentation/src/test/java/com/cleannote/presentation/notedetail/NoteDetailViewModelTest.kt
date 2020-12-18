package com.cleannote.presentation.notedetail

import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.BaseViewModelTest
import com.cleannote.presentation.Complete
import com.cleannote.presentation.OnError
import com.cleannote.presentation.OnSuccess
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.TextMode.DefaultMode
import com.cleannote.presentation.data.notedetail.TextMode.EditDoneMode
import com.cleannote.presentation.extensions.verifyExecute
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

   /* @AfterEach
    fun release(){
        viewModelState.removeObserver(stateObserver)
    }*/

    @Test
    fun updateNoteExecuteUseCase(){
        setNoteWithMode(DefaultMode, noteView)
        setNoteWithMode(EditDoneMode, noteView)
        verifyUpdateNoteExecute()
    }

    @Test
    fun updateNoteStateLoadingReturnNoData(){
        setNoteWithMode(DefaultMode, noteView)
        setNoteWithMode(EditDoneMode, noteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)
        assertViewModelUpdateNoteEqual(null)
    }

    @Test
    fun updateNoteStateSuccessReturnNote(){
        setNoteWithMode(DefaultMode, noteView)
        val updatedNoteView = noteView.copy(title = "updatedTitle")
        setNoteWithMode(EditDoneMode, updatedNoteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)

        onSuccessUpdateNote()
        verifyViewModelDataState(SUCCESS)
        assertViewModelUpdateNoteEqual(updatedNoteView)
    }

    @Test
    fun updateNoteStateErrorReturnThrowable(){
        val throwable = RuntimeException()
        setNoteWithMode(DefaultMode, noteView)
        val updatedNoteView = noteView.copy(title = "updatedTitle")
        setNoteWithMode(EditDoneMode, updatedNoteView)
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)

        onErrorUpdateNote(throwable)
        verifyViewModelDataState(ERROR)
        assertViewModelUpdateNoteHasThrowable(throwable)
    }

    @Test
    fun uploadImageThenUpdate(){
        setNoteWithMode(DefaultMode, noteView)
        val updateImagePath = "updateImagePath"
        whenUploadImage(updateImagePath,"2020-06-01 11:11:11")
        verifyUpdateNoteExecute()
        verifyViewModelDataState(LOADING)
        onSuccessUpdateNote()
        verifyViewModelDataState(SUCCESS)
        assertNoteImagesOfFirstImagePath(updateImagePath)
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

    private fun assertNoteImagesOfFirstImagePath(expectedPath: String){
        assertThat(viewModel.finalNote.value?.noteImages?.get(0)?.img_path, `is`(expectedPath))
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

    private fun setNoteWithMode(mode: TextMode, noteView: NoteView){
        viewModel.setNoteMode(mode, noteView)
        if (mode == EditDoneMode) setViewModelState(viewModel.updatedNote.value?.status)
    }

    private fun whenUploadImage(path: String, updateTime: String){
        viewModel.uploadImage(path, updateTime)
        setViewModelState(viewModel.updatedNote.value?.status)
    }

}