package com.cleannote.presentation.data.notedetail

sealed class DetailToolbarState {
    object TbExpanded : DetailToolbarState()
    object TbCollapse : DetailToolbarState()
}
