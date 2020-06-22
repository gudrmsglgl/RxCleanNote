package com.cleannote.presentation.data

sealed class ToolbarState {
    object TbExpanded: ToolbarState()
    object TbCollapse: ToolbarState()
}