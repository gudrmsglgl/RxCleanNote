package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.mapper.NoteEntityMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class NoteRemoteImpl @Inject constructor(private val noteService: NoteService,
                                         private val entityMapper: NoteEntityMapper):
    NoteRemote {

    override fun getNumNotes(): Flowable<List<NoteEntity>> {
        return noteService.getNotes(1)
            .map { noteModeles ->
                noteModeles.map {
                    entityMapper.mapFromRemote(it)
                }
            }
    }

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable {
        return noteService.insertNote(
            noteEntity.id,
            noteEntity.title,
            noteEntity.body,
            noteEntity.updated_at,
            noteEntity.created_at
        )
    }
}