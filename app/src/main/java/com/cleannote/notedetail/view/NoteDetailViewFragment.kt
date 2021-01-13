package com.cleannote.notedetail.view

import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailViewBinding
import com.cleannote.common.BaseFragment
import com.cleannote.extension.*
import com.cleannote.extension.toolbar.setMenuIconColor
import com.cleannote.extension.toolbar.setToolbar
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.presentation.data.notedetail.TextMode.DefaultMode
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.scrollChangeEvents
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.fragment_note_detail_view.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


class NoteDetailViewFragment(
    val viewModelFactory: ViewModelProvider.Factory,
    val requestManager: RequestManager) : BaseFragment<FragmentNoteDetailViewBinding>(R.layout.fragment_note_detail_view)
{

    private val viewModel:NoteDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPreviousFragmentNote()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timber("d","viewModelFactory: $viewModelFactory")
        timber("d", "viewModel: $viewModel")
        initBinding()
        initViewPager()
        initToolbar()
        appbarChangeSource()
    }

    override fun initBinding() {
        binding.vm = viewModel
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            val noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            viewModel.defaultMode(viewModel.finalNote() ?: noteUiModel.transNoteView())
        }
    }

    private fun initViewPager(){
        binding.imagePager.apply {
            val imageViewAdapter = ImageViewAdapter(requestManager)
            adapter = imageViewAdapter
        }
    }

    private fun initToolbar() {
        binding.detailViewToolbar.setToolbar(
            R.drawable.arrow_back,
            R.menu.menu_detail_view,
            View.OnClickListener {
                showToast("home")
            },
            Toolbar.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        findNavController().navigate(
                            R.id.action_noteDetailViewFragment_to_noteDetailFragment,
                            bundleOf(NOTE_DETAIL_BUNDLE_KEY to viewModel.finalNote()?.transNoteUiModel())
                        )
                        true
                    }
                    else -> {
                        showToast("home")
                        true
                    }
                }
            }
        )
    }

    private fun appbarChangeSource() = binding.appBarDetailView
        .offsetChanges()
        .map {
            (binding.appBarDetailView.y / binding.appBarDetailView.totalScrollRange).absoluteValue
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { offset ->
            changeViewPagerAlpha(offset)
            setStatusBar(offset)
            if (offset == 1.0f){
                appbarCollapseUI()
            } else if (offset >= 0.0f && offset < 1.0f){
                appbarExpandedUI()
            }
        }
        .addCompositeDisposable()

    private fun changeViewPagerAlpha(offset: Float) = with(binding){
        if (imagePager.isVisible) imagePager.alpha = 1 - (offset)
        else ivDvNoImages.alpha = 1 - (offset)
    }

    private fun appbarCollapseUI() = with(binding){
        detailViewToolbar.apply {
            title = viewModel.finalNote()?.title ?: ""
            setMenuIconColor(R.color.black)
            setBackgroundColor(Color.WHITE)
        }
        bottomSheet.setBackgroundResource(R.drawable.expand_bottom_sheet_background)
    }

    private fun appbarExpandedUI() = with(binding){
        detailViewToolbar.apply{
            title = ""
            if (imagePager.isVisible) setMenuIconColor(R.color.white) else setMenuIconColor(R.color.black)
            setBackgroundColor(Color.TRANSPARENT)
        }
        bottomSheet.setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
    }

    private fun setStatusBar(offset: Float){
        if (binding.ivDvNoImages.isVisible) {
            setStatusBarColor(R.color.transparent)
            setStatusBarTextBlack()
            return
        }
        if (offset == 1.0f){
            setStatusBarColor(R.color.white)
            setStatusBarTextBlack()
        } else if (offset >= 0.0f && offset < 1.0f){
            setStatusBarColor(R.color.transparent)
            setStatusBarTextTrans()
        }
    }

}