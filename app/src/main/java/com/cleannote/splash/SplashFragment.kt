package com.cleannote.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.cleannote.app.R
import com.cleannote.common.*
import com.cleannote.mapper.UserMapper
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.model.UserView
import com.cleannote.presentation.splash.SplashViewModel
import kotlinx.android.synthetic.main.fragment_splash.*

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val userMapper: UserMapper
): BaseFragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.tryLogin()
        logoClickListener()
        subscribeLoginResult()
    }

    private fun logoClickListener(){
        splash_logo.singleClick()
            .subscribe {
                showInputDialog(
                    getString(R.string.dialog_login),
                    inputDialogCallback
                )}
            .addCompositeDisposable()
    }

    private fun subscribeLoginResult() = viewModel.loginResult
        .observe(viewLifecycleOwner,
            Observer {
                if (it != null) handleLoginDS(it.status, it.data, it.message)
            })

    private fun handleLoginDS(
        state:State,
        data: List<UserView>?,
        message: String?
    ) = when (state) {
        is LOADING -> showLoadingProgressBar(true)
        is SUCCESS -> {
            showLoadingProgressBar(false)
            showSuccessLoginUser(data, message)
        }
        is ERROR -> {
            showLoadingProgressBar(false)
            showErrorMessage(message!!)
        }
    }

    private fun showSuccessLoginUser(data: List<UserView>?, message: String?){
        data?.let {
            val userView = it[0]
            val loginMessage = """
                | Welcome ${userView.nick} 
                | RxClean Note App""".trimMargin()
            showToast(loginMessage)
            findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
        }?: showErrorMessage(
            message!!,
            object : ButtonCallback{
                override fun confirmProceed() {
                    showRetryLoginDialog()
                }
                override fun cancelProceed() {}

                override fun inputValueReceive(value: String) {}
            })
    }

    private fun showRetryLoginDialog(){
        val retryMessage = getString(R.string.dialog_login_retry)
        showInputDialog(retryMessage, inputDialogCallback)
    }

    private val inputDialogCallback: ButtonCallback = object : ButtonCallback{
        override fun confirmProceed() {}

        override fun cancelProceed() {}

        override fun inputValueReceive(value: String) = with(viewModel) {
            setUserId(value)
            tryLogin()
        }
    }
}
