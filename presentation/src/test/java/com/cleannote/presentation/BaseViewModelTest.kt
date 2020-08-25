package com.cleannote.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.domain.interactor.UseCase
import com.cleannote.presentation.data.State
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.mockito.verification.VerificationMode

typealias OnSuccess<T> = (T) -> Unit
typealias OnError = (Throwable) -> Unit
typealias Complete = () -> Unit

abstract class BaseViewModelTest {

    lateinit var viewModelState: MutableLiveData<State>
    val stateObserver: Observer<State> = mock()

    fun setViewModelState(state: State?){
        viewModelState.value = state
    }

    fun verifyViewModelDataState(state: State?, verificationMode: VerificationMode? = null){
        if (verificationMode == null)
            verify(stateObserver).onChanged(state)
        else verify(stateObserver, verificationMode).onChanged(state)
    }


    fun <T, Param> UseCase<T, Param>.verifyExecute(
        onSuccessCaptor: KArgumentCaptor<OnSuccess<T>>,
        onErrorCaptor: KArgumentCaptor<OnError>,
        afterFinishedCaptor: KArgumentCaptor<Complete>,
        onCompleteCaptor: KArgumentCaptor<Complete>,
        paramCaptor: KArgumentCaptor<Param>
    ) = verify(this).execute(onSuccess = onSuccessCaptor.capture(),
                                            onError = onErrorCaptor.capture(),
                                            afterFinished = afterFinishedCaptor.capture(),
                                            onComplete = onCompleteCaptor.capture(),
                                            params = paramCaptor.capture()
    )


}