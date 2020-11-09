package com.cleannote.notedetail

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import com.afollestad.materialdialogs.utils.MDUtil.dimenPx
import com.afollestad.materialdialogs.utils.MDUtil.resolveColor
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailViewBinding
import com.cleannote.common.BaseFragment
import com.cleannote.extension.setMenuIconColor
import com.cleannote.extension.setToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*


class NoteDetailViewFragment
    : BaseFragment<FragmentNoteDetailViewBinding>(R.layout.fragment_note_detail_view)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initBottomSheetHeight()
        initBottomSheet()
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

    private fun initBottomSheet(){
        initBottomSheetHeight()
        bottomBehaviorSetup()
    }
    private fun initBottomSheetHeight(){
        binding.bottomSheet.apply {
            timber("d", "ToolbarHeight: ${dimenPx(R.dimen.toolbar_height)}")
            val setHeight = getWindowHeight() - dimenPx(R.dimen.toolbar_height)
            layoutParams.height = setHeight
        }
    }

    private fun bottomBehaviorSetup(){
        BottomSheetBehavior.from(binding.bottomSheet).apply {
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState){
                        STATE_EXPANDED -> {
                            binding.detailViewToolbar.title = "expanded"
                            binding.detailViewToolbar.setMenuIconColor(R.color.black)
                            binding.bottomSheet.setBackgroundResource(R.drawable.expand_bottom_sheet_background)
                        }
                        STATE_COLLAPSED -> {
                            binding.detailViewToolbar.title = ""
                            binding.detailViewToolbar.setMenuIconColor(R.color.white)
                            binding.bottomSheet.setBackgroundResource(R.drawable.collapse_bottom_sheet_background)
                        }
                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
            peekHeight = getWindowHeight() - dimenPx(R.dimen.detail_view_appbar_size)

        }
    }
}