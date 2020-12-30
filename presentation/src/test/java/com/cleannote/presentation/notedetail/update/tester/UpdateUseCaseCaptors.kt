package com.cleannote.presentation.notedetail.update.tester

import com.cleannote.domain.model.Note
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class UpdateUseCaseCaptors: ArgumentCaptors<Nothing>() {
    val param: KArgumentCaptor<Note> = argumentCaptor()
}