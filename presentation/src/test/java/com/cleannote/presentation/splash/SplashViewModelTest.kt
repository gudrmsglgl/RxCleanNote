/*
package com.cleannote.presentation.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.domain.model.User
import com.cleannote.presentation.data.State
import com.cleannote.presentation.mapper.UserMapper
import com.cleannote.presentation.model.UserView
import com.cleannote.presentation.test.InstantExecutorExtension
import com.cleannote.presentation.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.subscribers.DisposableSubscriber
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class SplashViewModelTest {

    lateinit var state: MutableLiveData<State>
    lateinit var stateObserver: Observer<State>
    lateinit var captor: KArgumentCaptor<DisposableSubscriber<List<User>>>
    lateinit var loginUseCase: Login
    lateinit var userMapper: UserMapper
    lateinit var viewModel: SplashViewModel

    @BeforeEach
    fun init(){
        state = MutableLiveData()
        stateObserver = mock()
        state.observeForever(stateObserver)
        captor = argumentCaptor()
        loginUseCase = mock()
        userMapper = mock()
        viewModel = SplashViewModel(loginUseCase, userMapper)
    }

    @AfterEach
    fun release(){
        state.removeObserver(stateObserver)
    }

    @Test
    fun loginUseCaseExecute(){
        viewModel.tryLogin()
        verify(loginUseCase).execute(any(), anyOrNull())
    }

    @Test
    fun loginUseCaseStateLoading(){
        viewModel.tryLogin()
        setCurrentState(getState())
        verify(loginUseCase).execute(any(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)
    }

    @Test
    fun loginUseCaseStateLoadingNoReturnAnyThing(){
        viewModel.tryLogin()
        setCurrentState(getState())
        verify(loginUseCase).execute(any(), anyOrNull())
        verify(stateObserver).onChanged(State.LOADING)
        assertThat( getMessage(), `is`(nullValue()))
        assertThat( getData(), `is`(nullValue()))
    }

    @Test
    fun loginUseCaseStateLoadingToSuccess(){
        viewModel.tryLogin()
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.LOADING)

        verify(loginUseCase).execute(captor.capture(), anyOrNull())
        captor.firstValue.onNext(UserFactory.makeUsers())
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.SUCCESS)
    }

    @Test
    fun loginUseCaseStateSuccessReturnDataNoMessage(){
        val users = UserFactory.makeUsers()
        val userViews = UserFactory.makeUserViews()
        userViews.forEachIndexed { index, userView ->
            stubUserMapperMapToView(userView, users[index])
        }

        viewModel.tryLogin()
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.LOADING)

        verify(loginUseCase).execute(captor.capture(), anyOrNull())
        captor.firstValue.onNext(users)
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.SUCCESS)

        assertThat(getData(), `is`(userViews))
        assertThat(getMessage(), `is`(nullValue()))
    }

    @Test
    fun loginUseCaseStateSuccessReturnMessageNoData(){
        val emptyUsers = UserFactory.makeEmptyUsers()

        viewModel.tryLogin()
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.LOADING)

        verify(loginUseCase).execute(captor.capture(), anyOrNull())
        captor.firstValue.onNext(emptyUsers)
        setCurrentState(getState())
        verify(stateObserver).onChanged(State.SUCCESS)

        assertThat(getData(), `is`(nullValue()))
        assertThat(getMessage(), `is`("Not User"))
    }

    private fun getData() = viewModel.loginResult.value?.data
    private fun getMessage() = viewModel.loginResult.value?.message
    private fun getState() = viewModel.loginResult.value?.status

    private fun setCurrentState(curState: State?){
        state.value = curState
    }

    private fun stubUserMapperMapToView(userView: UserView, user: User){
        whenever(userMapper.mapToView(user)).thenReturn(userView)
    }
}*/
