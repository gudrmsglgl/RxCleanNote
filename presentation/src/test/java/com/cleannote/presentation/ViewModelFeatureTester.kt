package com.cleannote.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.presentation.data.State
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.mockito.verification.VerificationMode

abstract class ViewModelFeatureTester<D, P>
{
    val state: MutableLiveData<State>  = MutableLiveData()
    private val stateObserver: Observer<State> = mock()

    init {
        state.removeObserver(stateObserver)
        state.observeForever(stateObserver)
    }

    fun verifyChangeState(state: State?, verificationMode: VerificationMode? = null): ViewModelFeatureTester<D, P> {
        if (verificationMode == null) verify(stateObserver).onChanged(state)
        else verify(stateObserver, verificationMode).onChanged(state)
        return this
    }

    fun setState(state: State?){
        this.state.value = state
    }

    abstract fun currentState(): State?

    abstract fun verifyUseCaseExecute(): ViewModelFeatureTester<D, P>

    abstract fun expectData(data: P?): ViewModelFeatureTester<D, P>

    abstract fun expectError(data: Throwable?): ViewModelFeatureTester<D, P>

    abstract fun expectState(state: State): ViewModelFeatureTester<D, P>

    abstract fun stubUseCaseOnSuccess(stub: D): ViewModelFeatureTester<D, P>

    abstract fun stubUseCaseOnError(stub: Throwable): ViewModelFeatureTester<D, P>

    abstract fun stubUseCaseOnComplete(): ViewModelFeatureTester<D, P>

}