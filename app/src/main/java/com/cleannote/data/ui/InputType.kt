package com.cleannote.data.ui

sealed class InputType {
    object Login: InputType()
    object NewNote: InputType()
}