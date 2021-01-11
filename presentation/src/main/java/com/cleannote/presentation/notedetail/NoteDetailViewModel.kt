package com.cleannote.presentation.notedetail

import android.util.Log
import androidx.lifecycle.*
import com.cleannote.domain.interactor.usecases.notedetail.NoteDetailUseCases
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.notedetail.BeforeAfterNoteView
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.extensions.createNoteImageView
import com.cleannote.presentation.extensions.transNote
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView

class NoteDetailViewModel
constructor(
    private val detailUseCases: NoteDetailUseCases
): ViewModel() {

    private val _finalNote = MutableLiveData<NoteView>()
    val finalNote: LiveData<NoteView>
        get() = _finalNote

    private val _updatedNote = MutableLiveData<DataState<NoteView>>()
    val updatedNote: LiveData<DataState<NoteView>>
        get() = _updatedNote

    private val _deletedNote = MutableLiveData<DataState<NoteView>>()
    val deletedNote: LiveData<DataState<NoteView>>
        get() = _deletedNote

    private val _detailToolbarState: MutableLiveData<DetailToolbarState> = MutableLiveData()
    val detailToolbarState: LiveData<DetailToolbarState>
        get() = _detailToolbarState

    private val _noteMode: MutableLiveData<TextMode> = MutableLiveData()
    val noteMode: LiveData<TextMode>
        get() = _noteMode

    fun setToolbarState(state: DetailToolbarState){
        _detailToolbarState.value = state
    }

    fun defaultMode(param: NoteView?){
        noteMode(DefaultMode)
        setFinalNote(param, isAsync = false)
    }

    fun editMode(){
        noteMode(EditMode)
    }

    fun editCancel(){
        noteMode(DefaultMode)
        setFinalNote(finalNote(), isAsync = false)
    }

    fun editDoneMode(param: NoteView){
        noteMode(EditDoneMode)
        executeUpdate(
            BeforeAfterNoteView(before = finalNote()!!, after = param)
        )
    }

    fun uploadImage(path: String, updateTime: String){
        executeUpdate(
            BeforeAfterNoteView(
                before = finalNote()!!,
                after = updatedFinalNoteOfImage(path, updateTime)
            )
        )
    }

    private fun updatedFinalNoteOfImage(
        path: String,
        updateTime: String
    ) = finalNote()!!.copy(
        updatedAt = updateTime,
        noteImages = finalNoteAddImage(path)
    )

    private fun executeUpdate(param: BeforeAfterNoteView){
        _updatedNote.postValue(DataState.loading())
        detailUseCases.updateNote.execute(
            onSuccess = {},
            onError = {
                setFinalNote(param.before, isAsync = true)
                _updatedNote.postValue(DataState.error(it))
            },
            onComplete = {
                setFinalNote(param.after, isAsync = true)
                _updatedNote.postValue(DataState.success(param.after))
            },
            params = param.after.transNote()
        )
    }

    private fun noteMode(mode: TextMode){
        _noteMode.value = mode
    }

    private fun setFinalNote(param: NoteView?, isAsync: Boolean){
        if (isAsync) _finalNote.postValue(param)
        else _finalNote.value = param
    }

    fun deleteNote(noteView: NoteView) {
        _deletedNote.postValue(DataState.loading())
        detailUseCases.deleteNote.execute(
            onSuccess = {},
            onError = {
                _deletedNote.postValue(DataState.error(it))
            },
            onComplete = {
                _deletedNote.postValue(DataState.success(noteView))
            },
            params = noteView.transNote()
        )
    }

    fun finalNote() = _finalNote.value

    private fun finalNoteAddImage(
        path: String
    ): List<NoteImageView> = finalNoteImages().apply {
        add(0, path.createNoteImageView(notePk = finalNote()!!.id))
    }

    private fun finalNoteImages() = finalNote()!!
        .noteImages
        ?.toMutableList() ?: mutableListOf()

    override fun onCleared() {
        super.onCleared()
        Log.d("RxCleanNote", "NoteDetailViewModel onCleared()")
    }
}