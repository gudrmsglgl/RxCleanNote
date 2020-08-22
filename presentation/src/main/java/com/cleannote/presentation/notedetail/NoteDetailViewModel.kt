package com.cleannote.presentation.notedetail

import android.util.Log
import androidx.lifecycle.*
import com.cleannote.presentation.data.notedetail.NoteTitleState
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.TextMode.DefaultMode
import com.cleannote.presentation.data.notedetail.TextMode.EditMode
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.DetailToolbarState.*

class NoteDetailViewModel constructor(): ViewModel() {

    private var _noteTitle: String = ""
    private var _noteBody: String = ""

    private val _detailToolbarState: MutableLiveData<DetailToolbarState> = MutableLiveData()
    val detailToolbarState: LiveData<DetailToolbarState>
        get() = _detailToolbarState

    val noteTitleState: LiveData<NoteTitleState>
        get() = Transformations.map(_detailToolbarState){
            if (it is TbExpanded) NtExpanded
            else NtCollapse
        }

    private val _noteMode: MutableLiveData<TextMode> = MutableLiveData(DefaultMode)
    val noteMode: LiveData<TextMode>
        get() = _noteMode

    fun setToolbarState(state: DetailToolbarState){
        _detailToolbarState.value = state
    }

    fun setNoteTitle(title: String) {
        _noteTitle = title.trimEnd()
    }

    fun getNoteTile() = _noteTitle

    fun setNoteMode(mode: TextMode){
        _noteMode.value = mode
    }

    fun isEditMode(): Boolean{
        return _noteMode.value is EditMode
    }

    fun setNoteBody(body: String){
        _noteBody = body.trimEnd()
    }

    fun getNoteBody() = _noteBody
}