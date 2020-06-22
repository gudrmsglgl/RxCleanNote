package com.cleannote.presentation.data

sealed class NoteTitleState {
    object NtExpanded: NoteTitleState()
    object NtCollapse: NoteTitleState()
}