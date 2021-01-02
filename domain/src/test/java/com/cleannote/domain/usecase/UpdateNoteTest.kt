
package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateNoteTest: BaseDomainTest<Completable, Note>(){

    private lateinit var updateNote: UpdateNote
    private val paramNote = NoteFactory.createSingleNote(title = "testUpdateNote")

    @BeforeEach
    fun setUp(){
        updateNote = UpdateNote(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositoryUpdateNote(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote).test()
        verifyRepositoryCallUpdateNote(paramNote)
    }

    @Test
    fun buildUseCaseCompletableComplete(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote)
            .test()
            .assertComplete()
    }

    @Test
    fun buildUseCaseUpdateNoteReturnNoValue(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote)
            .test()
            .assertNoValues()
    }

    private fun verifyRepositoryCallUpdateNote(param: Note){
        verify(repository).updateNote(param)
    }

    override fun whenBuildUseCase(param: Note): Completable {
        return updateNote.buildUseCaseCompletable(param)
    }

    override fun stubRepositoryReturnValue(param: Note?, stubValue: Completable) {
        whenever(repository.updateNote(param!!)).thenReturn(stubValue)
    }
}
