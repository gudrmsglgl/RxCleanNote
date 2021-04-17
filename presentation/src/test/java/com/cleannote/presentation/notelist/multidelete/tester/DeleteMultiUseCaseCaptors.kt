package com.cleannote.presentation.notelist.multidelete.tester

import com.cleannote.domain.model.Note
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class DeleteMultiUseCaseCaptors : ArgumentCaptors<Nothing>() {
    val paramCaptor: KArgumentCaptor<List<Note>> = argumentCaptor()
}
