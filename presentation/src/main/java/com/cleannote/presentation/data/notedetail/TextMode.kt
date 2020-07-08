package com.cleannote.presentation.data.notedetail

sealed class TextMode: Mode {
    object EditMode: TextMode(){
        override fun isEditMode(): Boolean = true
    }

    object DefaultMode: TextMode(){
        override fun isEditMode(): Boolean  = false
    }
}