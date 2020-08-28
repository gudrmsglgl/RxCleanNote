package com.cleannote.data.source

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.repository.NoteRemote
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

open class NoteRemoteDataStore
@Inject
constructor(
    private val noteRemote: NoteRemote
): NoteDataStore {

    override fun insertCacheNewNote(noteEntity: NoteEntity): Single<Long> {
        throw UnsupportedOperationException()
    }

    override fun insertRemoteNewNote(noteEntity: NoteEntity): Completable =
        noteRemote.insertRemoteNewNote(noteEntity)

    override fun login(userId: String): Flowable<List<UserEntity>> = noteRemote.login(userId)

    override fun searchNotes(queryEntity: QueryEntity): Flowable<List<NoteEntity>> =
        noteRemote.searchNotes(queryEntity)

    override fun saveNotes(notes: List<NoteEntity>, queryEntity: QueryEntity): Completable {
        throw UnsupportedOperationException()
    }

    override fun isCached(page: Int): Single<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun updateNote(noteEntity: NoteEntity): Completable {
        throw UnsupportedOperationException()
    }
}