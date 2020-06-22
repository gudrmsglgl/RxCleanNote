package com.cleannote.presentation.notedetail

import androidx.lifecycle.*
import com.cleannote.presentation.data.NoteTitleState
import com.cleannote.presentation.data.NoteTitleState.*
import com.cleannote.presentation.data.ToolbarState
import com.cleannote.presentation.data.ToolbarState.*

class NoteDetailViewModel constructor(): ViewModel() {

    private var _noteTitle: String = ""

    private val _toolbarState: MutableLiveData<ToolbarState> = MutableLiveData()
    val toolbarState: LiveData<ToolbarState>
        get() = _toolbarState

    val noteTitleState: LiveData<NoteTitleState>
        get() = Transformations.map(_toolbarState){
            if (it is TbExpanded) NtExpanded
            else NtCollapse
        }

    fun setToolbarState(state: ToolbarState){
        _toolbarState.value = state
    }

    fun setNoteTitle(title: String) {
        _noteTitle = title
    }

    fun getNoteTile() = _noteTitle
}