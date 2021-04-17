package com.cleannote.domain.interactor

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class CompletableUseCase<in Params> protected constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) : UseCase<Nothing, Params> {

    override var disposables: CompositeDisposable = CompositeDisposable()

    protected abstract fun buildUseCaseCompletable(params: Params? = null): Completable

    override fun execute(
        onSuccess: (t: Nothing) -> Unit,
        onError: (t: Throwable) -> Unit,
        afterFinished: () -> Unit,
        onComplete: () -> Unit,
        params: Params?
    ) {
        buildUseCaseCompletable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler)
            .doAfterTerminate(afterFinished)
            .subscribe(onComplete, onError)
            .also {
                addDisposable(it)
            }
    }
}
