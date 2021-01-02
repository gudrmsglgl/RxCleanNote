
package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteNoteTest: BaseDomainTest<Completable, Note>(){

    private lateinit var deleteNote: DeleteNote
    private val paramNote = NoteFactory.createSingleNote(title = "deleteNote")

    @BeforeEach
    fun setUp(){
        deleteNote = DeleteNote(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositoryDeleteNote(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote).test()
        verifyRepositoryCallDeleteNote(paramNote)
    }

    @Test
    fun buildUseCaseCompletableComplete(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote)
            .test()
            .assertComplete()
    }

    @Test
    fun buildUseCaseDeleteNoteReturnNoValue(){
        stubRepositoryReturnValue(paramNote, Completable.complete())
        whenBuildUseCase(paramNote)
            .test()
            .assertNoValues()
    }

    private fun verifyRepositoryCallDeleteNote(param: Note){
        verify(repository).deleteNote(param)
    }

    override fun stubRepositoryReturnValue(param: Note?, stubValue: Completable) {
        whenever(repository.deleteNote(param!!)).thenReturn(stubValue)
    }

    override fun whenBuildUseCase(param: Note): Completable {
        return deleteNote.buildUseCaseCompletable(param)
    }
}
