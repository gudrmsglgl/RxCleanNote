package com.cleannote.presentation.notelist.search

import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

class SearchUseCaseCaptors: ArgumentCaptors<List<Note>>() {
    val queryCaptor: KArgumentCaptor<Query> = argumentCaptor()
    fun capturedQuery() = queryCaptor.firstValue
}