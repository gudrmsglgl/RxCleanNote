package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteRemote {
    fun getNumNotes(): Flowable<List<NoteEntity>>
    fun insertRemoteNewNote(noteEntity: NoteEntity): Completable
}