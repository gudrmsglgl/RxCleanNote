package com.cleannote.domain.interactor

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


abstract class FlowableUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread): UseCase<T, Params> {

    override var disposables: CompositeDisposable = CompositeDisposable()

    protected abstract fun buildUseCaseFlowable(params: Params? = null): Flowable<T>

    override fun execute(
        onSuccess: (t: T) -> Unit,
        onError: (t: Throwable) -> Unit,
        afterFinished: () -> Unit,
        onComplete: () -> Unit,
        params: Params?
    ) {
        buildUseCaseFlowable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler)
            .doOnComplete(onComplete)
            .doAfterTerminate(afterFinished)
            .subscribe(onSuccess, onError)
            .also {
                addDisposable(it)
            }
    }

}