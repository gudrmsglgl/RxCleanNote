package com.cleannote.presentation.notelist.next.tester

import com.cleannote.domain.model.Query
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class NextUseCaseCaptors : ArgumentCaptors<Boolean>() {
    val paramCaptor: KArgumentCaptor<Query> = argumentCaptor()
}
