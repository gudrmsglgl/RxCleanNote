package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InsertNoteNoteTest : BaseDomainTest<Single<Long>, Note>() {

    private lateinit var insertNewNote: InsertNewNote
    private val paramNote = NoteFactory.createSingleNote(id = "#1", title = "insertTitle", body = "insertBody")

    @BeforeEach
    fun setup() {
        insertNewNote = InsertNewNote(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositoryInsertNewNote() {
        stubRepositoryReturnValue(paramNote, Single.just(1L))
        whenBuildUseCase(paramNote).test()
        verifyRepositoryCallInsertNewNote(paramNote)
    }

    @Test
    fun buildUseCaseSingleComplete() {
        stubRepositoryReturnValue(paramNote, Single.just(1L))
        whenBuildUseCase(paramNote)
            .test()
            .assertComplete()
    }

    @Test
    fun buildUseCaseInsertNewNoteReturnRow() {
        val insertedRow = 1L
        stubRepositoryReturnValue(paramNote, Single.just(insertedRow))
        whenBuildUseCase(paramNote)
            .test()
            .assertValue(insertedRow)
    }

    private fun verifyRepositoryCallInsertNewNote(param: Note) {
        verify(repository).insertNewNote(param)
    }

    override fun whenBuildUseCase(param: Note): Single<Long> {
        return insertNewNote.buildUseCaseSingle(param)
    }

    override fun stubRepositoryReturnValue(param: Note?, stubValue: Single<Long>) {
        whenever(repository.insertNewNote(param!!)).thenReturn(stubValue)
    }
}
