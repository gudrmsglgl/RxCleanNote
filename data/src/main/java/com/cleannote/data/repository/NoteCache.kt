package com.cleannote.data.repository

import com.cleannote.data.model.NoteEntity
import io.reactivex.Flowable
import io.reactivex.Single

interface NoteCache {
    fun getNumNotes(): Flowable<List<NoteEntity>>
    fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long>
}