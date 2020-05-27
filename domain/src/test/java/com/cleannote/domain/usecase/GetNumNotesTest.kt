package com.cleannote.domain.usecase

import com.cleannote.domain.interfactor.executor.PostExecutionThread
import com.cleannote.domain.interfactor.executor.ThreadExecutor
import com.cleannote.domain.interfactor.repository.NoteRepository
import com.cleannote.domain.interfactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetNumNotesTest {

    private lateinit var getNumNotes: GetNumNotes

    private lateinit var mockThreadExecutor: ThreadExecutor
    private lateinit var mockPostExecutionThread: PostExecutionThread
    private lateinit var mockNoteRepository: NoteRepository

    private lateinit var notes: List<Note>

    @BeforeEach
    fun setUP() {
        notes = NoteFactory.createNoteList(10)
        mockThreadExecutor = mock()
        mockPostExecutionThread = mock()
        mockNoteRepository = mock(){
            on { getNumNotes() } doReturn Flowable.just(notes)
        }
        getNumNotes = GetNumNotes(mockNoteRepository, mockThreadExecutor, mockPostExecutionThread)
    }

    @Test
    fun buildUseCaseObservableCallsRepository() {
        getNumNotes.buildUseCaseObservable(null)
        verify(mockNoteRepository).getNumNotes()
    }

    @Test
    fun buildUseCaseObservableComplete(){
        val testObserver =
            getNumNotes.buildUseCaseObservable(null).test()
        testObserver.assertComplete()
    }

    @Test
    fun buildUseCaseObservableReturnData(){
        val testObserver =
            getNumNotes.buildUseCaseObservable(null).test()

        testObserver.assertValue(notes)
    }
}