package com.cleannote.splash

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.cleannote.app.R
import com.cleannote.common.UIController
import com.cleannote.mapper.UserMapper
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.splash.SplashViewModel
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val userMapper: UserMapper
): Fragment() {

    private val TAG = "RxCleanNote"
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }
    lateinit var uiController: UIController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiController = context as UIController
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement com.cleannote.common.UIController" )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.tryLogin()
        subscribeLoginResult()
    }

    private fun subscribeLoginResult() = viewModel.loginResult
        .observe(viewLifecycleOwner,
            Observer {
                when (it.status) {
                    LOADING -> {
                        Log.d(TAG, "login_loading...")
                        uiController.displayProgressBar(true)
                    }

                    SUCCESS -> {
                        Log.d(TAG, "login_result...")
                        uiController.displayProgressBar(false)
                        it.data?.let { userViews ->
                            userViews.forEach {
                                Log.d(TAG, "login_data: ${userMapper.mapToUiModel(it)}")
                            }
                        }

                        it.message?.let {
                            Log.d(TAG, "login_message: $it...")
                        }

                    }

                    ERROR -> {
                        uiController.displayProgressBar(false)
                        it.message?.let {
                            Log.d(TAG, "login_error: $it...")
                        }
                    }
                }
            })


}
