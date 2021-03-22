package com.cleannote.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.verification.VerificationMode

// C: Child ClassType, D: UseCase Domain Return Type, Param: UseCase Param, R: ViewModel DataState Type
@Suppress("UNCHECKED_CAST")
abstract class ViewModelFeatureTester<C, D, Param, R>(private val captors: ArgumentCaptors<D>)
{
    val state: MutableLiveData<State>  = MutableLiveData()
    private val stateObserver: Observer<State> = mock()

    init {
        state.removeObserver(stateObserver)
        state.observeForever(stateObserver)
    }

    fun verifyChangeState(state: State?, verificationMode: VerificationMode? = null): C{
        if (verificationMode == null) verify(stateObserver).onChanged(state)
        else verify(stateObserver, verificationMode).onChanged(state)
        return this as C
    }

    fun setState(state: State?){
        this.state.value = state
    }

    open fun expectData(data: R?): C{
        if (data == null)
            assertThat(vmCurrentData()?.data, `is`(nullValue()))
        else
            assertThat(vmCurrentData()?.data, `is`(data))
        return this as C
    }

    fun expectError(data: Throwable?): C{
        assertThat(vmCurrentData()?.throwable, `is`(data))
        return this as C
    }

    fun expectState(state: State): C{
        assertThat(vmCurrentData()?.status, `is`(state))
        return this as C
    }

    fun stubUseCaseOnSuccess(stub: D): C{
       captors.onSuccessInvoke(stub)
       setState(currentState())
       return this as C
    }

    fun stubUseCaseOnError(stub: Throwable): C{
        captors.onErrorInvoke(stub)
        setState(currentState())
        return this as C
    }

    fun stubUseCaseOnComplete(): C{
        captors.onCompleteInvoke()
        setState(currentState())
        return this as C
    }

    abstract fun verifyUseCaseExecute(): C
    abstract fun vmCurrentData(): DataState<R>?
    abstract fun currentState(): State?
}