package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.cleannote.domain.usecase.common.CompletableUseCaseBuilder
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteNoteTest: BaseDomainTest<Note>(), CompletableUseCaseBuilder<Note> {

    private lateinit var deleteNote: DeleteNote
    private val paramNote = NoteFactory.createSingleNote(title = "deleteNote")
    @BeforeEach
    fun setUp(){
        repository = mock {
            on { deleteNote(paramNote) } doReturn Completable.complete()
        }
        mockRxSchedulers()
        deleteNote = DeleteNote(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun deleteNoteCallRepository(){
        whenBuildUseCase(paramNote).test()
        verifyRepositoryCall(paramNote)
    }

    @Test
    fun deleteNoteAssertComplete(){
        whenBuildUseCase(paramNote)
            .test()
            .assertComplete()
            .assertNoValues()
    }

    override fun verifyRepositoryCall(param: Note?) {
        verify(repository).deleteNote(paramNote)
    }

    override fun whenBuildUseCase(param: Note): Completable {
        return deleteNote.buildUseCaseCompletable(param)
    }
}