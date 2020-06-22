package com.cleannote.presentation.data

sealed class TextMode: Mode {
    object EditMode: TextMode(){
        override fun isEditMode(): Boolean = true
    }

    object DefaultMode: TextMode(){
        override fun isEditMode(): Boolean  = false
    }
}