package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.extensions.transNoteEntities
import com.cleannote.remote.extensions.transUserEntities
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class NoteRemoteImpl
@Inject constructor(private val noteService: NoteService): NoteRemote {

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable {
        return noteService.insertNote(
            noteEntity.id,
            noteEntity.title,
            noteEntity.body,
            noteEntity.updated_at,
            noteEntity.created_at
        )
    }

    override fun login(userId: String): Flowable<List<UserEntity>> {
        return noteService.login(userId)
            .map {
                it.transUserEntities()
            }
    }

    override fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>> {
        return noteService.searchNotes(
            queryEntity.page,
            queryEntity.limit,
            queryEntity.sort,
            queryEntity.order,
            queryEntity.like,
            queryEntity.like
        ).onErrorResumeNext(
            Flowable.just(emptyList())
        ).map {
            it.transNoteEntities()
        }
    }

    override fun updateNote(noteEntity: NoteEntity): Completable {
        throw UnsupportedOperationException()
    }
}