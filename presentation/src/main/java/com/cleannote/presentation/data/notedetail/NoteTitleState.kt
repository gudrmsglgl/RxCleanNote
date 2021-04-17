package com.cleannote.presentation.data.notedetail

sealed class NoteTitleState {
    object NtExpanded : NoteTitleState()
    object NtCollapse : NoteTitleState()
}
