package com.cleannote.ui.base

import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import io.mockk.every
import io.reactivex.Completable
import io.reactivex.Single

interface RepoStubber {
    val repository: NoteRepository

    fun stubNextPageExist(stub: Boolean) = every {
        repository.nextPageExist(any())
    } returns Single.just(stub)

    fun stubNoteRepositorySearchNotes(data: Single<List<Note>>, query: Query? = null) {
        every {
            repository.searchNotes(query ?: any())
        } returns data
    }

    fun stubNoteRepositorySearchNotes(query: Query? = null, vararg data: Single<List<Note>>){
        every {
            repository.searchNotes(query ?: any())
        } returnsMany listOf(*data)
    }

    fun stubThrowableNoteRepositorySearchNotes(throwable: Throwable, query: Query? = null) {
        every {
            repository.searchNotes(query ?: any())
        } returns Single.error(throwable)
    }

    fun stubNoteRepositoryUpdate(){
        every {
            repository.updateNote(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryUpdate(throwable: Throwable){
        every {
            repository.updateNote(any())
        }.returns(Completable.error(throwable))
    }

    fun stubNoteRepositoryDelete(){
        every {
            repository.deleteNote(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryDelete(throwable: Throwable){
        every {
            repository.deleteNote(any())
        }.returns(Completable.error(throwable))
    }

    fun stubNoteRepositoryDeleteMultiNotes(){
        every {
            repository.deleteMultipleNotes(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryDeleteMultiNotes(throwable: Throwable){
        every {
            repository.deleteMultipleNotes(any())
        }.returns(Completable.error(throwable))
    }
}