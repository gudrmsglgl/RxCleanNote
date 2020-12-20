package com.cleannote.presentation.notelist.delete.Tester

import com.cleannote.domain.model.Note
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class DeleteUseCaseCaptors: ArgumentCaptors<Nothing>() {
    val paramCaptors:KArgumentCaptor<Note> = argumentCaptor()
    fun noteParam() = paramCaptors.firstValue
}