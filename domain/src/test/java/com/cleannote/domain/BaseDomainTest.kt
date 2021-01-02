package com.cleannote.domain

import com.cleannote.domain.interactor.UseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach

abstract class BaseDomainTest<D, Param> {
    protected lateinit var repository: NoteRepository

    protected lateinit var threadExecutor: ThreadExecutor
    protected lateinit var postExecutionThread: PostExecutionThread

    @BeforeEach
    fun baseSetup(){
        repository = mock()
    }

    fun mockRxSchedulers(){
        threadExecutor = mock()
        postExecutionThread = mock()
    }

    abstract fun whenBuildUseCase(param: Param): D
    abstract fun stubRepositoryReturnValue(param: Param?, stubValue: D)
}