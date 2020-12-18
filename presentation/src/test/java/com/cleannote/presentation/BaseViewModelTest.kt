package com.cleannote.presentation

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.domain.Constants
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.interactor.UseCase
import com.cleannote.presentation.data.State
import com.cleannote.presentation.notelist.NoteListViewModel
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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

    @BeforeEach
    open fun setup(){
        viewModelState = MutableLiveData()
        viewModelState.observeForever(stateObserver)
    }

    @AfterEach
    open fun release(){
        viewModelState.removeObserver(stateObserver)
    }


    fun verifyViewModelDataState(state: State?, verificationMode: VerificationMode? = null){
        if (verificationMode == null)
            verify(stateObserver).onChanged(state)
        else verify(stateObserver, verificationMode).onChanged(state)
    }

}