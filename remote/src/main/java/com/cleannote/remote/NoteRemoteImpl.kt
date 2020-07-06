package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.model.UserEntity
import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.mapper.NoteEntityMapper
import com.cleannote.remote.mapper.UserEntityMapper
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class NoteRemoteImpl @Inject constructor(private val noteService: NoteService,
                                         private val noteEntityMapper: NoteEntityMapper,
                                         private val userEntityMapper: UserEntityMapper):
    NoteRemote {

    override fun getNumNotes(): Flowable<List<NoteEntity>> {
        return noteService.getNotes(1)
            .map { noteModeles ->
                noteModeles.map {
                    noteEntityMapper.mapFromRemote(it)
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

    override fun login(userId: String): Flowable<List<UserEntity>> {
        return noteService.login(userId).map { userModels ->
            userModels.map {
                userEntityMapper.mapFromRemote(it)
            }
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
        ).map { noteModels ->
            noteModels.map { noteEntityMapper.mapFromRemote(it) }
        }
    }
}