package com.cleannote.presentation.notelist.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.presentation.data.State
import com.cleannote.presentation.model.NoteView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.verification.VerificationMode

abstract class ViewModelFeatureTester<T>
{
    val state: MutableLiveData<State>  = MutableLiveData()
    private val stateObserver: Observer<State> = mock()

    init {
        state.removeObserver(stateObserver)
        state.observeForever(stateObserver)
    }

    fun verifyChangeState(state: State?, verificationMode: VerificationMode? = null):ViewModelFeatureTester<T>{
        if (verificationMode == null) verify(stateObserver).onChanged(state)
        else verify(stateObserver, verificationMode).onChanged(state)
        return this
    }

    fun setState(state: State?){
        this.state.value = state
    }

    abstract fun verifyUseCaseExecute(): ViewModelFeatureTester<T>

    abstract fun expectData(data: T?): ViewModelFeatureTester<T>

    abstract fun expectError(data: Throwable?): ViewModelFeatureTester<T>

    abstract fun expectState(state: State): ViewModelFeatureTester<T>

    abstract fun stubUseCaseOnSuccess(stub: T): ViewModelFeatureTester<T>

    abstract fun stubUseCaseOnError(stub: Throwable): ViewModelFeatureTester<T>

}