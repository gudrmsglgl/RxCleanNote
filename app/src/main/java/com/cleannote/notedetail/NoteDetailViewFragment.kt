package com.cleannote.notedetail

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailViewBinding
import com.cleannote.common.BaseFragment
import com.cleannote.extension.setToolbar


class NoteDetailViewFragment
    : BaseFragment<FragmentNoteDetailViewBinding>(R.layout.fragment_note_detail_view)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
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
}