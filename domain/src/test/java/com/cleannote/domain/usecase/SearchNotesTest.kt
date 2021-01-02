package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.test.factory.NoteFactory
import com.cleannote.domain.test.factory.QueryFactory
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchNotesTest: BaseDomainTest<Single<List<Note>>, Query>() {

    private lateinit var searchNotes: SearchNotes

    private val defaultQuery = QueryFactory.makeDefaultQuery()

    @BeforeEach
    fun setup(){
        mockRxSchedulers()
        searchNotes = SearchNotes(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositorySearchNotes(){
        stubRepositoryReturnValue(
            param = defaultQuery,
            stubValue = Single.just(NoteFactory.createNoteList(2))
        )
        whenBuildUseCase(defaultQuery).test()
        verifyRepositoryCallSearchNotes(defaultQuery)
    }

    @Test
    fun buildUseCaseSingleComplete(){
        stubRepositoryReturnValue(
            param = defaultQuery,
            stubValue = Single.just(NoteFactory.createNoteList(2))
        )
        whenBuildUseCase(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    fun buildUseCaseSearchQueryReturnNote(){
        val stubNotes = NoteFactory.createNoteList(2)
        stubRepositoryReturnValue(
            param = defaultQuery,
            stubValue = Single.just(stubNotes)
        )
        whenBuildUseCase(defaultQuery)
            .test()
            .assertValue(stubNotes)
    }

    private fun verifyRepositoryCallSearchNotes(param: Query?) {
        verify(repository).searchNotes(param!!)
    }

    override fun whenBuildUseCase(param: Query): Single<List<Note>> {
        return searchNotes.buildUseCaseSingle(param)
    }

    override fun stubRepositoryReturnValue(param: Query?, stubValue: Single<List<Note>>) {
        whenever(repository.searchNotes(param!!)).thenReturn(stubValue)
    }
}