package com.cleannote.notedetail

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.presentation.notedetail.NoteDetailViewModel

abstract class BaseDetailFragment<DataBinding : ViewDataBinding>(
    private val factory: ViewModelProvider.Factory,
    @LayoutRes layoutRes: Int
): BaseFragment<DataBinding>(layoutRes) {


    //val viewModel by activityViewModels<NoteDetailViewModel> { factory }


}