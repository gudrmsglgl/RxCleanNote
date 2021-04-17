package com.cleannote.presentation.notelist.insert.tester

import com.cleannote.domain.model.Note
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class InsertUseCaseCaptors : ArgumentCaptors<Long>() {
    val paramCaptor: KArgumentCaptor<Note> = argumentCaptor()
}
