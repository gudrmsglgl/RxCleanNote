package com.cleannote.domain

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.BeforeEach

abstract class BaseDomainTest<D, Param> {
    protected lateinit var repository: NoteRepository

    protected lateinit var threadExecutor: ThreadExecutor
    protected lateinit var postExecutionThread: PostExecutionThread

    @BeforeEach
    fun baseSetup(){
        mockRxSchedulers()
        repository = mock()
    }

    private fun mockRxSchedulers(){
        threadExecutor = mock()
        postExecutionThread = mock()
    }

    abstract fun whenBuildUseCase(param: Param): D
    abstract fun stubRepositoryReturnValue(param: Param?, stubValue: D)
}