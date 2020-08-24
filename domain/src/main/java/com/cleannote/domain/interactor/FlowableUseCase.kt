package com.cleannote.domain.interactor

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber


abstract class FlowableUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread): UseCase<DisposableSubscriber<T>, Params> {

    private var disposables: CompositeDisposable = CompositeDisposable()

    protected abstract fun buildUseCaseFlowable(params: Params? = null): Flowable<T>

    override fun execute(observer: DisposableSubscriber<T>, params: Params?) {
        val observable = this.buildUseCaseFlowable(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler) as Flowable<T>
        addDisposable(observable.subscribeWith(observer))
    }

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun addDisposable(disposable: Disposable) {
        if (disposables.isDisposed){
            disposables = CompositeDisposable()
        }
        disposables.add(disposable)
    }

}