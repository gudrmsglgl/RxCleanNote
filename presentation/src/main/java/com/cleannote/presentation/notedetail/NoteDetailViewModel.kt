package com.cleannote.presentation.notedetail

import androidx.lifecycle.*
import com.cleannote.presentation.data.NoteTitleState
import com.cleannote.presentation.data.NoteTitleState.*
import com.cleannote.presentation.data.TextMode
import com.cleannote.presentation.data.TextMode.DefaultMode
import com.cleannote.presentation.data.TextMode.EditMode
import com.cleannote.presentation.data.ToolbarState
import com.cleannote.presentation.data.ToolbarState.*

class NoteDetailViewModel constructor(): ViewModel() {

    private var _noteTitle: String = ""
    private var _noteBody: String = ""

    private val _toolbarState: MutableLiveData<ToolbarState> = MutableLiveData()
    val toolbarState: LiveData<ToolbarState>
        get() = _toolbarState

    val noteTitleState: LiveData<NoteTitleState>
        get() = Transformations.map(_toolbarState){
            if (it is TbExpanded) NtExpanded
            else NtCollapse
        }

    private val _noteMode: MutableLiveData<TextMode> = MutableLiveData(DefaultMode)
    val noteMode: LiveData<TextMode>
        get() = _noteMode

    fun setToolbarState(state: ToolbarState){
        _toolbarState.value = state
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