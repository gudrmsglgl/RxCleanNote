package com.cleannote.domain.interactor

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface UseCase<T, in Params> {
    var disposables: CompositeDisposable

    fun execute(onSuccess: (t: T) -> Unit,
                onError: (t: Throwable) -> Unit,
                afterFinished: () -> Unit = {},
                onComplete: () -> Unit = {},
                params: Params?)

    fun dispose(){
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun addDisposable(disposable: Disposable){
        if (disposables.isDisposed){
            disposables = CompositeDisposable()
        }
        disposables.add(disposable)
    }
}