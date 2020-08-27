package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.test.factory.NoteFactory
import com.cleannote.domain.test.factory.QueryFactory
import com.cleannote.domain.usecase.common.FlowableUseCaseBuilder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchNotesTest: BaseDomainTest<Query>(), FlowableUseCaseBuilder<List<Note>, Query> {

    private lateinit var searchNotes: SearchNotes

    private lateinit var notes: List<Note>
    private lateinit var searchedNote: List<Note>
    private val search: String = "testTitle"
    private val defaultQuery = QueryFactory.makeDefaultQuery()
    private val searchQuery = QueryFactory.makeSearchQuery(search = search)

    @BeforeEach
    fun setup(){
        notes = NoteFactory.createNoteList(5)
        searchedNote = listOf(NoteFactory.createSingleNote(title = search))
        repository = mock{
            on { searchNotes(defaultQuery) }.thenReturn(Flowable.just(notes))
            on { searchNotes(searchQuery) }.thenReturn(Flowable.just(searchedNote))
        }
        mockRxSchedulers()
        searchNotes = SearchNotes(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepository(){
        whenBuildUseCase(defaultQuery)
        verifyRepositoryCall(defaultQuery)
    }

    @Test
    fun buildUseCaseObservableComplete(){
        val test = whenBuildUseCase(defaultQuery).test()
        verifyRepositoryCall(defaultQuery)
        test.assertComplete()
    }

    @Test
    fun buildUseCaseDefaultQueryReturnNotes(){
        val test = whenBuildUseCase(defaultQuery).test()
        test.assertValue(notes)
    }

    @Test
    fun buildUseCaseSearchQuery(){
        val test = whenBuildUseCase(searchQuery).test()
        test.assertValue(searchedNote)
    }

    override fun whenBuildUseCase(param: Query): Flowable<List<Note>> {
        return repository.searchNotes(param)
    }

    override fun verifyRepositoryCall(param: Query?) {
        verify(repository).searchNotes(param!!)
    }
}