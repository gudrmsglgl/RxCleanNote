package com.cleannote.notedetail.view

import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailViewBinding
import com.cleannote.common.BaseFragment
import com.cleannote.extension.*
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

    private val viewModel:NoteDetailViewModel by viewModels { viewModelFactory }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        getPreviousFragmentNote()
        initViewPager()
        initToolbar()
        appbarChangeSource()
    }

    private fun initBinding() = binding.apply {
        vm = viewModel
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            val noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            viewModel.setNoteMode(DefaultMode, viewModel.finalNote.value ?: noteUiModel.transNoteView())
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
                        showToast("edit")
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
            if (offset == 1.0f){
                appbarCollapseUI()
                setStatusBarColor(R.color.white)
                setStatusBarTextBlack()
            } else if (offset >= 0.0f && offset < 1.0f){
                appbarExpandedUI()
                setStatusBarColor(R.color.transparent)
                setStatusBarTextTrans()
            }
        }
        .addCompositeDisposable()

    private fun changeViewPagerAlpha(offset: Float) = with(binding){
        imagePager.alpha = 1 - (offset)
    }

    private fun appbarCollapseUI() = with(binding){
        detailViewToolbar.apply {
            title = viewModel.finalNote.value?.title ?: ""
            setMenuIconColor(R.color.black)
            setBackgroundColor(Color.WHITE)
        }
        bottomSheet.setBackgroundResource(R.drawable.expand_bottom_sheet_background)
    }

    private fun appbarExpandedUI() = with(binding){
        detailViewToolbar.apply{
            title = ""
            setMenuIconColor(R.color.white)
            setBackgroundColor(Color.TRANSPARENT)
        }
        bottomSheet.setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
    }

}