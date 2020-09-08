package com.cleannote.domain.interactor.repository

import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteRepository {

    fun insertNewNote(note: Note): Single<Long>
    fun login(userId: String): Flowable<List<User>>
    fun searchNotes(query: Query): Flowable<List<Note>>
    fun updateNote(note: Note): Completable
    fun deleteNote(note: Note): Completable
}