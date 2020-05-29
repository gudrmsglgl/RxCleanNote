package com.cleannote.data

import com.cleannote.data.mapper.NoteMapper
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.domain.interfactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Flowable
import io.reactivex.Single
import java.lang.Exception
import javax.inject.Inject

class NoteDataRepository
@Inject
constructor(
    private val factory: NoteDataStoreFactory,
    private val noteMapper: NoteMapper
): NoteRepository{

    override fun getNumNotes(): Flowable<List<Note>> = factory.retrieveRemoteDataStore()
        .getNumNotes()
        .map { listNoteEntity ->
            listNoteEntity.map {
                noteMapper.mapFromEntity(it)
            }
        }

    override fun insertNewNote(note: Note): Single<Long> = factory.retrieveCacheDataStore()
        .insertCacheNewNote(noteMapper.mapToEntity(note))
        .map {
            if (it > 0) {
                factory.retrieveRemoteDataStore().insertRemoteNewNote(noteMapper.mapToEntity(note))
                it
            } else {
                throw Exception()
            }
        }
        .onErrorReturn { -1L }





}