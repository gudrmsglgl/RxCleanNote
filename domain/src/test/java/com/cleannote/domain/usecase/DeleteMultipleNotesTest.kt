package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.cleannote.domain.usecase.common.CompletableUseCaseBuilder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteMultipleNotesTest: BaseDomainTest<List<Note>>(), CompletableUseCaseBuilder<List<Note>> {

    lateinit var deleteMultipleNotesTest: DeleteMultipleNotes
    @BeforeEach
    fun setUp(){
        repository = mock ()
        mockRxSchedulers()
        deleteMultipleNotesTest = DeleteMultipleNotes(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun deleteMultipleNotesCallRepository(){
        val deleteNotes = NoteFactory.createNoteList(3)
        stubCompleteDeleteMultiple(param = deleteNotes)
        whenBuildUseCase(deleteNotes).test()
        verifyRepositoryCall(deleteNotes)
    }

    @Test
    fun deleteMultipleNotesAssertComplete(){
        val deleteNotes = NoteFactory.createNoteList(3)
        stubCompleteDeleteMultiple(param = deleteNotes)
        whenBuildUseCase(deleteNotes)
            .test()
            .assertComplete()
            .assertNoValues()
    }

    override fun verifyRepositoryCall(param: List<Note>?) {
        verify(repository).deleteMultipleNotes(param!!)
    }

    override fun whenBuildUseCase(param: List<Note>): Completable {
        return repository.deleteMultipleNotes(param)
    }

    private fun stubCompleteDeleteMultiple(param: List<Note>){
        whenever(repository.deleteMultipleNotes(param)).thenReturn(Completable.complete())
    }
}