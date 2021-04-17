package com.cleannote.domain.interactor

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class SingleUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) : UseCase<T, Params> {

    override var disposables: CompositeDisposable = CompositeDisposable()

    abstract fun buildUseCaseSingle(params: Params? = null): Single<T>

    override fun execute(
        onSuccess: (t: T) -> Unit,
        onError: (t: Throwable) -> Unit,
        afterFinished: () -> Unit,
        onComplete: () -> Unit,
        params: Params?
    ) {
        buildUseCaseSingle(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler)
            .doAfterTerminate(afterFinished)
            .subscribe(onSuccess, onError)
            .also {
                addDisposable(it)
            }
    }
}
