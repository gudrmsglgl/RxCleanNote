package com.cleannote.domain

import com.cleannote.domain.interactor.UseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable

abstract class BaseDomainTest<T,Param> {
    protected lateinit var repository: NoteRepository

    protected lateinit var threadExecutor: ThreadExecutor
    protected lateinit var postExecutionThread: PostExecutionThread

    fun mockRxSchedulers(){
        threadExecutor = mock()
        postExecutionThread = mock()
    }

    abstract fun whenBuildFlowableUseCase(param: Param?): Flowable<T>

    abstract fun verifyRepositoryCall(param: Param?)
}