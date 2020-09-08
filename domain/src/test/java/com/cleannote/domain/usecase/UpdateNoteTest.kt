package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.cleannote.domain.usecase.common.CompletableUseCaseBuilder
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateNoteTest: BaseDomainTest<Note>(), CompletableUseCaseBuilder<Note> {

    private lateinit var updateNote: UpdateNote
    private val paramNote = NoteFactory.createSingleNote(title = "testUpdateNote")

    @BeforeEach
    fun setUp(){
        repository = mock {
            on { updateNote(paramNote) } doReturn Completable.complete()
        }
        mockRxSchedulers()
        updateNote = UpdateNote(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun updateNoteCallRepository(){
        whenBuildUseCase(paramNote).test()
        verifyRepositoryCall(paramNote)
    }

    @Test
    fun updateNoteAssertComplete(){
        val test = whenBuildUseCase(paramNote).test()
        verifyRepositoryCall(paramNote)
        test.assertComplete()
    }

    @Test
    fun updateNoteAssertReturnUnit(){
        val test = whenBuildUseCase(paramNote).test()
        verifyRepositoryCall(paramNote)
        test.assertNoValues()
    }

    override fun whenBuildUseCase(param: Note): Completable {
        return repository.updateNote(param)
    }

    override fun verifyRepositoryCall(param: Note?) {
        verify(repository).updateNote(param!!)
    }
}