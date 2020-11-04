package com.cleannote.presentation.notedetail

import androidx.lifecycle.*
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.presentation.common.BaseViewModel
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.notedetail.NoteTitleState
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.DetailToolbarState.*
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.extensions.createNoteImageView
import com.cleannote.presentation.extensions.transNote
import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView

class NoteDetailViewModel
constructor(
    private val updateNote: UpdateNote,
    private val deleteNote: DeleteNote
): BaseViewModel(updateNote, deleteNote) {

    val finalNote = MutableLiveData<NoteView>()
    private lateinit var tempNote: NoteView

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

    fun setNoteMode(mode: TextMode, noteParam: NoteView?){
        _noteMode.value = mode
        if (mode == DefaultMode) finalNote.value = noteParam!!
        else if (mode == EditDoneMode){
            tempNote = finalNote.value!!

            _updatedNote.postValue(DataState.loading())
            updateNote.execute(
                onSuccess = {},
                onError = {
                    finalNote.postValue(tempNote)
                    _updatedNote.postValue(DataState.error(it))
                },
                onComplete = {
                    finalNote.postValue(noteParam)
                    _updatedNote.postValue(DataState.success(noteParam))
                },
                params = noteParam?.transNote()
            )
        }
    }

    fun deleteNote(noteView: NoteView) {
        _deletedNote.postValue(DataState.loading())
        deleteNote.execute(
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

    fun uploadImage(path: String, updateTime: String){
        tempNote = finalNote.value!!.copy(
            updated_at = updateTime,
            noteImages = copyNoteImages(path)
        )

        _updatedNote.postValue(DataState.loading())
        updateNote.execute(
            onSuccess = {},
            onError = {
                finalNote.postValue(finalNote.value)
                _updatedNote.postValue(DataState.error(it))
            },
            onComplete = {
                finalNote.postValue(tempNote)
                _updatedNote.postValue(DataState.success(tempNote))
            },
            params = tempNote.transNote()
        )

    }

    private fun copyNoteImages(path: String): List<NoteImageView> {
        val list =
            finalNote.value!!.noteImages?.toMutableList() ?: mutableListOf()
        list.add(0, path.createNoteImageView(notePk = finalNote.value!!.id))
        return list
    }
}