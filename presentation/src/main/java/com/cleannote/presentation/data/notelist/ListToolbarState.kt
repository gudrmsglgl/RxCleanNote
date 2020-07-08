package com.cleannote.presentation.data.notelist

sealed class ListToolbarState {
    object SearchState: ListToolbarState()
    object MultiSelectState: ListToolbarState()
}