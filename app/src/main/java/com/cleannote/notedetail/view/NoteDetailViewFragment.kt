package com.cleannote.notedetail.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailViewBinding
import com.cleannote.common.BaseFragment
import com.cleannote.extension.*
import com.cleannote.extension.toolbar.setToolbar
import com.cleannote.extension.toolbar.setUI
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.IS_EXECUTE_INSERT
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_SCROLL_TOP_KEY
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


class NoteDetailViewFragment(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFragment<FragmentNoteDetailViewBinding>(R.layout.fragment_note_detail_view)
{
    private val viewModel
            by navGraphViewModels<NoteDetailViewModel>(R.id.nav_detail_graph) { viewModelFactory }

    private val pagerCallback = object : ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            binding.indicator.selectIndicator(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreviousFragmentNote()
        initBinding()
        initImagePager()
        initIndicator()
        initToolbar()
        appbarOffsetChangeSource()
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

    private fun initImagePager() = binding
        .imagePager
        .apply {
            adapter = ImageViewAdapter(requestManager)
            registerOnPageChangeCallback(pagerCallback)
        }

    private fun initIndicator() = binding
        .indicator
        .create(
            size = viewModel.finalNote()?.noteImages?.size,
            defaultRes = R.drawable.bg_default_indicator,
            selectRes = R.drawable.bg_select_indicator,
            selectPosition = 0
        )

    private fun initToolbar() = binding
        .tbDetailView
        .setToolbar(
            homeIcon = R.drawable.ic_dv_back,
            menuRes = R.menu.menu_detail_view,
            onHomeClick = {
                navPopBackStack()
            },
            onMenuClick = {
                navDetailEditFragment()
                true
            }
        )

    private fun appbarOffsetChangeSource() = binding
        .appBarDetailView
        .offsetChanges()
        .map {
            binding.appBarDetailView.offsetChangeRatio()
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { offset ->
            changeIndicatorAlpha(offset)
            changeViewPagerAlpha(offset)
            changeStatusBar(offset)
            changeToolbar(offset)
            changeBottomSheet(offset)
        }
        .addCompositeDisposable()

    private fun changeIndicatorAlpha(offset: Float) = with(binding){
        indicator.alpha = 1 - offset
    }

    private fun changeViewPagerAlpha(offset: Float) = with(binding){
        if (imagePager.isVisible) imagePager.alpha = 1 - offset
        else ivEmpty.alpha = 1 - offset
    }

    private fun changeStatusBar(offset: Float){
        if (binding.ivEmpty.isVisible) {
            statusBarOnPagerEmpty()
            return
        }
        if (offset == 1.0f){
            statusBarOnAppBarCollapse()
        } else if (offset >= 0.0f && offset < 1.0f){
            statusBarOnAppBarExpand()
        }
    }

    private fun changeToolbar(offset: Float){
        if (offset == 1.0f){
            toolbarUiOnAppbarCollapse()
        } else if (offset >= 0.0f && offset < 1.0f){
            toolbarUiOnAppbarExpand()
        }
    }

    private fun toolbarUiOnAppbarCollapse() = with(binding){
        tbDetailView.setUI(
            titleParam = viewModel.finalNote()?.title,
            iconColor = R.color.black,
            backgroundColor = Color.WHITE
        )
    }

    private fun toolbarUiOnAppbarExpand() = with(binding){
        tbDetailView.setUI(
            titleParam = null,
            iconColor = if(imagePager.isVisible) R.color.white else R.color.black,
            backgroundColor = Color.TRANSPARENT
        )
    }

    private fun changeBottomSheet(offset: Float) = with(binding.bottomSheet){
        if (offset == 1.0f){
            setBackgroundResource(R.drawable.expand_bottom_sheet_background)
        } else if (offset >= 0.0f && offset < 1.0f){
            setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
        }
    }

    private fun statusBarOnPagerEmpty(){
        setStatusBarColor(R.color.transparent)
        setStatusBarTextBlack()
    }

    private fun statusBarOnAppBarCollapse(){
        setStatusBarColor(R.color.white)
        setStatusBarTextBlack()
    }

    private fun statusBarOnAppBarExpand(){
        setStatusBarColor(R.color.transparent)
        setStatusBarTextTrans()
    }

    private fun navDetailEditFragment(){
        findNavController().navigate(
            R.id.action_noteDetailViewFragment_to_noteDetailEditFragment,
            bundleOf(NOTE_DETAIL_BUNDLE_KEY to viewModel.finalNote()?.transNoteUiModel())
        )
    }

    fun navPopBackStack(){
        if (isExecuteInsert()) reqScrollTop()
        findNavController().popBackStack()
    }

    private fun isExecuteInsert() = arguments?.let {
        return it.getBoolean(IS_EXECUTE_INSERT)
    } ?: false

    private fun reqScrollTop(){
        setFragmentResult(REQUEST_KEY_ON_BACK, bundleOf(REQ_SCROLL_TOP_KEY to true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.imagePager.unregisterOnPageChangeCallback(pagerCallback)
    }
}