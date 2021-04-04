package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.extensions.*
import com.cleannote.remote.extensions.NETWORK
import com.cleannote.remote.extensions.TIMEOUT
import com.cleannote.remote.extensions.applyRetryPolicy
import com.cleannote.remote.model.NoteModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class NoteRemoteImpl
@Inject constructor(private val noteService: NoteService): NoteRemote {

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable {
        return noteService.insertNote(
            noteEntity.id,
            noteEntity.title,
            noteEntity.body,
            noteEntity.updatedAt,
            noteEntity.createdAt
        )
    }

    override fun login(userId: String): Flowable<List<UserEntity>> {
        return noteService.login(userId)
            .map {
                it.transUserEntities()
            }
    }

    override fun searchNotes(queryEntity: QueryEntity): Single<List<NoteEntity>> {
        return noteService.searchNotes(
            queryEntity.page,
            queryEntity.limit,
            queryEntity.sort,
            queryEntity.order,
            queryEntity.like,
            queryEntity.like
        ).compose(
            applyRetryPolicy(TIMEOUT, NETWORK, SERVICE_UNAVAILABLE) {
                Single.just(emptyList<NoteModel>())
            }
        ).map {
            it.transNoteEntities()
        }
    }

    override fun updateNote(noteEntity: NoteEntity): Completable {
        throw UnsupportedOperationException()
    }
}