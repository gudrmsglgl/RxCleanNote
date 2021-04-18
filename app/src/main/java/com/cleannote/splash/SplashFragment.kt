package com.cleannote.splash

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.airbnb.lottie.LottieDrawable
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentSplashBinding
import com.cleannote.common.*
import com.cleannote.extension.changeTextColor
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import com.jakewharton.rxbinding4.view.layoutChangeEvents

class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash),
    MotionLayout.TransitionListener
{

    companion object {
        const val LOGO_MAX_FRAME = 59
    }

    private val viewModel: NoteListViewModel
        by navGraphViewModels(R.id.nav_app_graph) { viewModelFactory }

    private val noteObserver: Observer<DataState<List<NoteView>>> = Observer {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLottieLogo()
        initTextLogoGradation()
        binding.splashFragmentContainer.setTransitionListener(this)
    }

    private fun initTextLogoGradation() {
        binding.tvRx.layoutChangeEvents()
            .subscribe {
                val rxColorArray = intArrayOf(Color.parseColor("#FBBBE2"), Color.parseColor("#B80083"), Color.parseColor("#410055"))
                val textShader: Shader = LinearGradient(
                    0f, 0f, it.view.width.toFloat(), it.view.height.toFloat(), rxColorArray, null, Shader.TileMode.CLAMP
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

    private fun ovalDefaultToYellow(tv: TextView) {
        tv.background = ContextCompat.getDrawable(this@SplashFragment.requireContext(), R.drawable.bg_oval_solid)
        tv.changeTextColor(R.color.black)
    }

    override fun onTransitionStarted(motion: MotionLayout, startId: Int, endId: Int) {
        if (startId == R.id.const_delay) {
            viewModel.noteList.observe(viewLifecycleOwner, noteObserver)
        }
    }

    override fun onTransitionChange(p0: MotionLayout?, startId: Int, p2: Int, progress: Float) {
        if (startId == R.id.const_start && progress > 0.2f) {
            ovalDefaultToYellow(binding.tvOvalRx)
            ovalDefaultToYellow(binding.tvOvalJet)
            ovalDefaultToYellow(binding.tvOvalClean)
        }
    }

    override fun onTransitionCompleted(p0: MotionLayout?, state: Int) {
        if (state == R.id.end) {
            findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
            viewModel.noteList.removeObserver(noteObserver)
        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        //not used
    }
}
