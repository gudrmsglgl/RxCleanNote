package com.cleannote.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentSplashBinding
import com.cleannote.common.*
import com.cleannote.data.ui.InputType
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.model.UserView
import com.cleannote.presentation.splash.SplashViewModel
import kotlinx.android.synthetic.main.fragment_splash.*
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.tryLogin()
        //findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
        subscribeLoginResult()
    }

    override fun initBinding() {
        println("TODO: dataBinding")
    }



    private fun subscribeLoginResult() = viewModel.loginResult
        .observe(viewLifecycleOwner,
            Observer {
                if (it != null){
                    when (it.status) {
                        is LOADING -> showLoadingProgressBar(true)
                        is SUCCESS -> {
                            showLoadingProgressBar(false)
                        }
                        is ERROR -> {
                            showLoadingProgressBar(false)
                            showErrorMessage("login fail")
                        }
                    }
                }
            })

}
