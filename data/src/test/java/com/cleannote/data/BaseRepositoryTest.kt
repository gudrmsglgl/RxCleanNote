package com.cleannote.data

import com.cleannote.data.mapper.NoteMapper
import com.cleannote.data.mapper.QueryMapper
import com.cleannote.data.mapper.UserMapper
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class BaseRepositoryTest {

    lateinit var noteDataRepository: NoteDataRepository

    lateinit var noteMapper: NoteMapper
    lateinit var userMapper: UserMapper
    lateinit var queryMapper: QueryMapper


    infix fun Query.stubTo(queryEntity: QueryEntity){
        whenever(queryMapper.mapToEntity(this)).thenReturn(queryEntity)
    }

    infix fun NoteEntity.stubTo(note: Note){
        whenever(noteMapper.mapFromEntity(this)).thenReturn(note)
    }

    infix fun Note.stubTo(noteEntity: NoteEntity){
        whenever(noteMapper.mapToEntity(this)).thenReturn(noteEntity)
    }

    infix fun NoteDataStore.stubSearchNotes(stubEntities: Pair<QueryEntity, List<NoteEntity>>){
        whenever(this.searchNotes(stubEntities.first)).thenReturn(Flowable.just(stubEntities.second))
    }

    infix fun NoteDataStore.stubSaveNotes(stubs: Triple<List<NoteEntity>, QueryEntity, Completable>){
        whenever(this.saveNotes(stubs.first, stubs.second)).thenReturn(stubs.third)
    }

    infix fun NoteDataStore.stubUpdateNote(stub: Pair<NoteEntity, Completable>){
        whenever(this.updateNote(stub.first)).thenReturn(stub.second)
    }

    fun whenInsertNote(note: Note) = noteDataRepository.insertNewNote(note)
    fun NoteDataStore.verifyInsertNote(noteEntity: NoteEntity){
        if (this is NoteCacheDataStore)
            verify(this).insertCacheNewNote(noteEntity)
        else
            verify(this).insertRemoteNewNote(noteEntity)
    }

    fun whenSearchNotes(query: Query) = noteDataRepository.searchNotes(query)

    fun whenUpdateNote(note: Note) = noteDataRepository.updateNote(note)
}