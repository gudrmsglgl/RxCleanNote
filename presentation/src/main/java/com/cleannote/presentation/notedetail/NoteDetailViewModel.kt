package com.cleannote.presentation.notedetail

import android.util.Log
import androidx.lifecycle.*
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.presentation.common.BaseViewModel
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.notedetail.NoteTitleState
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.DetailToolbarState.*
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import io.reactivex.subscribers.DisposableSubscriber

class NoteDetailViewModel
constructor(
    private val updateNote: UpdateNote,
    private val deleteNote: DeleteNote,
    private val noteMapper: NoteMapper
): BaseViewModel(updateNote) {

    private lateinit var note: NoteView
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

    val noteTitleState: LiveData<NoteTitleState>
        get() = Transformations.map(_detailToolbarState){
            if (it is TbExpanded) NtExpanded
            else NtCollapse
        }

    private val _noteMode: MutableLiveData<TextMode> = MutableLiveData()
    val noteMode: LiveData<TextMode>
        get() = _noteMode

    fun setToolbarState(state: DetailToolbarState){
        _detailToolbarState.value = state
    }

    fun setNoteMode(mode: TextMode){
        _noteMode.value = mode
    }

    fun setNote(noteParam: Pair<NoteView, TextMode>){
        val curNoteView = noteParam.first
        val curMode = noteParam.second
        setNoteMode(curMode)

        tempNote = curNoteView

        if (curMode is EditDoneMode){
            _updatedNote.postValue(DataState.loading())
            updateNote.execute(
                onSuccess = {},
                onError = {
                    _updatedNote.postValue(DataState(State.ERROR, note, it.message))
                },
                onComplete = {
                    note = tempNote
                    _updatedNote.postValue(DataState.success(note))
                },
                params = noteMapper.mapFromView(tempNote)
            )
        }
    }


    fun deleteNote(noteView: NoteView) {
        _deletedNote.postValue(DataState.loading())
        deleteNote.execute(
            onSuccess = {},
            onError = {
                _deletedNote.postValue(DataState.error("delete note fail"))
            },
            onComplete = {
                _deletedNote.postValue(DataState.success(noteView))
            },
            params = noteMapper.mapFromView(noteView)
        )
    }

    fun isEditMode(): Boolean{
        return _noteMode.value is EditMode
    }

}