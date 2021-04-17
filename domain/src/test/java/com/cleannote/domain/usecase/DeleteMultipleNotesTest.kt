
package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteMultipleNotesTest : BaseDomainTest<Completable, List<Note>>() {

    private lateinit var deleteMultipleNotes: DeleteMultipleNotes
    private val paramNotes: List<Note> = NoteFactory.createNoteList(3)

    @BeforeEach
    fun setUp() {
        deleteMultipleNotes = DeleteMultipleNotes(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositoryDeleteMultipleNotes() {
        stubRepositoryReturnValue(paramNotes, Completable.complete())
        whenBuildUseCase(paramNotes).test()
        verifyRepositoryCallDeleteMultipleNotes(paramNotes)
    }

    @Test
    fun buildUseCaseCompletableComplete() {
        stubRepositoryReturnValue(paramNotes, Completable.complete())
        whenBuildUseCase(paramNotes)
            .test()
            .assertComplete()
    }

    @Test
    fun buildUseCaseDeleteMultipleNotesReturnNoValue() {
        stubRepositoryReturnValue(paramNotes, Completable.complete())
        whenBuildUseCase(paramNotes)
            .test()
            .assertNoValues()
    }

    private fun verifyRepositoryCallDeleteMultipleNotes(param: List<Note>) {
        verify(repository).deleteMultipleNotes(param)
    }

    override fun stubRepositoryReturnValue(param: List<Note>?, stubValue: Completable) {
        whenever(repository.deleteMultipleNotes(param!!)).thenReturn(stubValue)
    }

    override fun whenBuildUseCase(param: List<Note>): Completable {
        return deleteMultipleNotes.buildUseCaseCompletable(param)
    }
}
