package com.cleannote.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.domain.model.User
import com.cleannote.presentation.common.BaseViewModel
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.mapper.UserMapper
import com.cleannote.presentation.model.UserView

class SplashViewModel constructor(private val login: Login,
                                  private val userMapper: UserMapper): BaseViewModel(login) {

    private val TAG = "RxCleanNote"
    private var loginId: String = "gudrms"

    private val _loginResult: MutableLiveData<DataState<List<UserView>>> = MutableLiveData()
    val loginResult: LiveData<DataState<List<UserView>>>
        get() = _loginResult

    fun tryLogin(){
        _loginResult.value = DataState.loading()
        login.execute(
            onSuccess = {
                _loginResult.postValue(DataState.success(
                    data = setUser(it)))
            },
            onError = {
                _loginResult.postValue(DataState.error(it))
            },
            params =  loginId)
    }

    fun setUserId(id: String){
       loginId = id
    }

    private fun setUser(users: List<User>) =
        if (users.isEmpty()) null
        else users.map { userMapper.mapToView(it) }

    private fun setMessage(users: List<User>) =
        if (users.isEmpty()) "Not User"
        else null

}