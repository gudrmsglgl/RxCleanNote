package com.cleannote.data

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.repository.NoteDataStore
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.stub.NoteCacheDataStoreStubber
import com.cleannote.data.test.stub.NoteRemoteDataStoreStubber
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.verification.VerificationMode

abstract class BaseCacheDataTest {

    lateinit var noteDataRepository: NoteDataRepository

    fun whenDataRepositoryInsertNote(note: Note) = noteDataRepository.insertNewNote(note)

    fun whenDataRepositoryLogin(userId: String) = noteDataRepository.login(userId)

    fun whenDataRepositorySearchNotes(query: Query) = noteDataRepository.searchNotes(query)

    fun whenUpdateNote(note: Note) = noteDataRepository.updateNote(note)

    fun whenDataRepositoryDeleteNote(note: Note) = noteDataRepository.deleteNote(note)

    fun whenDataRepositoryDeleteMultiNotes(notes: List<Note>) = noteDataRepository.deleteMultipleNotes(notes)

}