package com.cleannote.splash

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentSplashBinding
import com.cleannote.common.*
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.splash.SplashViewModel

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackground()
         //viewModel.tryLogin()
        //findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
        subscribeLoginResult()
    }

    fun setBackground() = binding.splashFragmentContainer.setTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        }

        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        }

        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        }

        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        }
    })

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
                            showErrorDialog("login fail")
                        }
                    }
                }
            })

}
