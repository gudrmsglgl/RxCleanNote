package com.cleannote.presentation.notedetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.usecases.notedetail.NoteDetailUseCases
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.SingleLiveEvent
import com.cleannote.presentation.data.notedetail.BeforeAfterNoteView
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.TextMode
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

    private val _updatedNote = SingleLiveEvent<DataState<NoteView>>()
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

    fun editDoneMode(param: NoteView) = finalNote()?.let {
        noteMode(EditDoneMode)
        executeUpdate(
            BeforeAfterNoteView(before = it, after = param)
        )
    }

    fun uploadImage(path: String, updateTime: String) = finalNote()?.let {
        executeUpdate(
            BeforeAfterNoteView(
                before = it,
                after = it.transAddImage(path, updateTime)
            )
        )
    }

    private fun NoteView.transAddImage(
        path: String,
        updateTime: String
    ) = this.copy(
        updatedAt = updateTime,
        noteImages = addImage(path, this.id)
    )

    fun deleteImage(path: String, updateTime: String) = finalNote()?.let{
        executeUpdate(
            BeforeAfterNoteView(
                before = it,
                after = it.transDeletedImage(path, updateTime)
            )
        )
    }

    private fun NoteView.transDeletedImage(
        path: String,
        updateTime: String
    ) = this.copy(
        updatedAt = updateTime,
        noteImages = removeImage(path)
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

    private fun addImage(path: String, notePk: String): List<NoteImageView> = finalNoteImages()
        .apply {
            add(0, path.createNoteImageView(notePk))
        }

    private fun removeImage(path: String): List<NoteImageView> = finalNoteImages()
        .apply {
            val targetImage = find { it.imagePath == path }
            remove(targetImage)
        }

    private fun finalNoteImages() = finalNote()
        ?.noteImages
        ?.toMutableList()
        ?: mutableListOf()

    fun finalNote() = _finalNote.value
    fun isTitleModified(currentTitle: String) = finalNote()?.title == currentTitle
    fun isBodyModified(currentBody: String) = finalNote()?.body == currentBody

    override fun onCleared() {
        super.onCleared()
        detailUseCases.disposeUseCases()
        Log.d("RxCleanNote", "NoteDetailViewModel onCleared()")
    }
}