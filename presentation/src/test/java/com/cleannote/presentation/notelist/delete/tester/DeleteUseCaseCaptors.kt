package com.cleannote.presentation.notelist.delete.tester

import com.cleannote.domain.model.Note
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class DeleteUseCaseCaptors: ArgumentCaptors<Nothing>() {
    val paramCaptor: KArgumentCaptor<Note> = argumentCaptor()
}