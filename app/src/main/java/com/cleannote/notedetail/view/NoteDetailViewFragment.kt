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
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
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

        app_bar_detail_view
            .offsetChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { offset ->
                // offset -559(collapse) ~ 0(expand)
                //expand
                changeViewPagerAlpha()
                changeMenu()
            }
            .addCompositeDisposable()

        //initBottomSheet()
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
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
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

    private fun changeViewPagerAlpha() = with(binding){
        val offsetAlpha = appBarDetailView.y / appBarDetailView.totalScrollRange
        imagePager.alpha = 1 - (offsetAlpha*-1)
    }

    private fun changeMenu() = with(binding) {
        val offset = (appBarDetailView.y / appBarDetailView.totalScrollRange).absoluteValue
        timber("d", "offset: ${offset}")
        if (offset == 1.0f){
            detailViewToolbar.apply {
                title = "expanded"
                setMenuIconColor(R.color.black)
                setBackgroundColor(Color.WHITE)
            }
            bottomSheet.setBackgroundResource(R.drawable.expand_bottom_sheet_background)
        } else if (offset >= 0.0f && offset < 1.0f){
            detailViewToolbar.apply{
                title = ""
                setMenuIconColor(R.color.white)
                setBackgroundColor(Color.TRANSPARENT)
            }
            bottomSheet.setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
        }
    }

    private fun initBottomSheet(){
        initBottomSheetHeight()
        bottomBehaviorSetup()
    }
    private fun initBottomSheetHeight(){
        binding.bottomSheet.apply {
            val setHeight = getWindowHeight() - dimenPx(R.dimen.toolbar_height)
            layoutParams.height = setHeight
        }
    }

    private fun bottomBehaviorSetup(){
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = getWindowHeight() + dimenPx(R.dimen.toolbar_height) - dimenPx(R.dimen.detail_view_appbar_size)

            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState){
                        STATE_EXPANDED -> {

                            /*binding.floatBtn
                                .animate()
                                .translationY(-dimenPx(R.dimen.detail_view_appbar_size).toFloat())
                                .alpha(0.1.toFloat())
                                .start()*/

                            //binding.appBarDetailView.setExpanded(false)
                            binding.detailViewToolbar.apply {
                                title = "expanded"
                                setMenuIconColor(R.color.black)
                                setBackgroundColor(Color.WHITE)
                            }
                            binding.bottomSheet.setBackgroundResource(R.drawable.expand_bottom_sheet_background)
                        }
                        STATE_COLLAPSED -> {

                            /*binding.floatBtn
                                .animate()
                                .translationY(0.toFloat())
                                .alpha(1.toFloat())
                                .start()*/

                            //binding.appBarDetailView.setExpanded(true)
                            binding.detailViewToolbar.apply{
                                title = ""
                                setMenuIconColor(R.color.white)
                                setBackgroundColor(Color.TRANSPARENT)
                            }
                            binding.bottomSheet.setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
                        }
                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    //timber("d", "bottomSheetSlideOffset: $slideOffset")
                    binding.floatBtn.alpha = 1 - slideOffset

                    /*val ani = binding.imagePager
                        .animate()
                        .setInterpolator(AccelerateInterpolator())
                        .alpha(1.toFloat()-slideOffset)
                        .start()*/
                }
            })
        }
    }
}