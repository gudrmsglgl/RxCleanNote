package com.cleannote.domain.interactor

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

abstract class SingleUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) {

    private var disposables: CompositeDisposable = CompositeDisposable()

    abstract fun buildUseCaseSingle(params: Params? = null): Single<T>

    open fun execute(observer: DisposableSingleObserver<T>, params: Params? = null){
        val singleSource = this.buildUseCaseSingle(params)
            .subscribeOn(Schedulers.from(threadExecutor))
            .observeOn(postExecutionThread.scheduler) as Single<T>
        addDisposable(singleSource.subscribeWith(observer))
    }

    private fun getCompositeDisposable(): CompositeDisposable{
        if (disposables.isDisposed){
            disposables = CompositeDisposable()
        }
        return disposables
    }

    fun dispose(){
        if (!disposables.isDisposed) disposables.dispose()
    }

    private fun addDisposable(disposable: Disposable) {
        getCompositeDisposable().add(disposable)
    }
}