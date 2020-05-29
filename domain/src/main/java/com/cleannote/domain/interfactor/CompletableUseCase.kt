package com.cleannote.domain.interfactor

import com.cleannote.domain.interfactor.executor.PostExecutionThread
import com.cleannote.domain.interfactor.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

abstract class CompletableUseCase<in Params> protected constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread) {

    private val subscription: Disposable = Disposables.empty()

    protected abstract fun buildUseCaseCompletable(params: Params): Completable

    fun execute(params: Params): Completable = this.buildUseCaseCompletable(params)
        .subscribeOn(Schedulers.from(threadExecutor))
        .observeOn(postExecutionThread.scheduler)

    fun unsubscribe() {
        if (!subscription.isDisposed) {
            subscription.dispose()
        }
    }

}