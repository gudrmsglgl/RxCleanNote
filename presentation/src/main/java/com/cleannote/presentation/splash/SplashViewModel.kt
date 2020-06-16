package com.cleannote.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.domain.model.User
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.mapper.UserMapper
import com.cleannote.presentation.model.UserView
import io.reactivex.subscribers.DisposableSubscriber

class SplashViewModel constructor(private val login: Login,
                                  private val userMapper: UserMapper): ViewModel() {

    private val TAG = "RxCleanNote"
    private var loginId: String = "gudrms"

    private val _loginResult: MutableLiveData<DataState<List<UserView>>> = MutableLiveData()
    val loginResult: LiveData<DataState<List<UserView>>>
        get() = _loginResult

    fun tryLogin(){
        _loginResult.value = DataState.loading()
        login.execute(LoginResultObserver(), loginId)
    }

    inner class LoginResultObserver: DisposableSubscriber<List<User>>(){
        override fun onComplete() {
        }

        override fun onNext(t: List<User>) {
            _loginResult.postValue(DataState.success(
                data = if (t.isEmpty()){
                    null
                } else {
                    t.map { userMapper.mapToView(it) }
                },
                message = if (t.isEmpty()){
                    "Not User"
                } else {
                    null
                }
            ))
        }

        override fun onError(t: Throwable?) {
            _loginResult.postValue(DataState.error(t?.message))
        }
    }

    fun setUserId(id: String){
       loginId = id
    }
}