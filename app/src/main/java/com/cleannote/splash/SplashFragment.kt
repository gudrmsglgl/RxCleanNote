package com.cleannote.splash

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieDrawable
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentSplashBinding
import com.cleannote.common.*
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.splash.SplashViewModel
import com.jakewharton.rxbinding4.view.layoutChangeEvents


class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    companion object {
        const val LOGO_MAX_FRAME = 59
    }

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLottieLogo()
        initRxText()
         //viewModel.tryLogin()
        //findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
        subscribeLoginResult()
    }

    private fun initRxText(){
        binding.tvRx.layoutChangeEvents()
            .subscribe {
                val rxColorArray = intArrayOf(Color.parseColor("#FBBBE2"), Color.parseColor("#B80083"),Color.parseColor("#410055"))
                val textShader: Shader = LinearGradient(
                    0f, 0f, it.view.width.toFloat(), it.view.height.toFloat(), rxColorArray,null, Shader.TileMode.CLAMP
                )
                binding.tvRx.paint.shader = textShader
            }
        binding.tvNote.layoutChangeEvents()
            .subscribe {
                val noteColorArray = intArrayOf(
                    Color.parseColor("#64B678"),
                    Color.parseColor("#478AEA"),
                    Color.parseColor("#8446CC")
                )
                val textShader: Shader = LinearGradient(
                    0f, 0f, it.view.width.toFloat(), it.view.height.toFloat(), noteColorArray, null, Shader.TileMode.CLAMP
                )
                binding.tvNote.paint.shader = textShader
            }
    }

    private fun initLottieLogo() = binding.androidLogo.apply {
        addAnimatorUpdateListener {
            if (binding.androidLogo.frame == LOGO_MAX_FRAME) {
                setMinProgress(0.9f)
                speed = 3f
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        }
        playAnimation()
    }

    override fun initBinding() {
        println("TODO: dataBinding")
    }

    private fun subscribeLoginResult() = viewModel.loginResult
        .observe(viewLifecycleOwner,
            Observer {
                if (it != null) {
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
