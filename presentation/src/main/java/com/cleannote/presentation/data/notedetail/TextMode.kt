package com.cleannote.presentation.data.notedetail

sealed class TextMode: Mode {
    object InitMode: TextMode(){
        override fun isEditMode(): Boolean  = false
    }

    object EditMode: TextMode(){
        override fun isEditMode(): Boolean = true
    }

    object EditDoneMode: TextMode(){
        override fun isEditMode(): Boolean  = false
    }
}