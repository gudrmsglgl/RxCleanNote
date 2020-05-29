package com.cleannote.domain.interfactor

import com.cleannote.domain.interfactor.executor.PostExecutionThread
import com.cleannote.domain.interfactor.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

abstract class SingleUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) {

    private val disposables = CompositeDisposable()

    abstract fun buildUseCaseSingle(params: Params? = null): Single<T>

    open fun execute(observer: DisposableSingleObserver<T>, params: Params? = null){
        val singleSource = this.buildUseCaseSingle(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler) as Single<T>
        addDisposables(singleSource.subscribeWith(observer))
    }

    fun dispose(){
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun addDisposables(disposable: Disposable) = disposables.add(disposable)
}