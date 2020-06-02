package com.cleannote.domain.interactor.repository

import com.cleannote.domain.model.Note
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteRepository {

    fun getNumNotes(): Flowable<List<Note>>
    fun insertNewNote(note: Note): Single<Long>

}