package com.cleannote.domain.interfactor.repository

import com.cleannote.domain.model.Note
import io.reactivex.Flowable

interface NoteRepository {

    fun getNumNotes(): Flowable<List<Note>>

}