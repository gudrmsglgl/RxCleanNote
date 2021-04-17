package com.cleannote.common

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.cleannote.notedetail.edit.NoteDetailEditFragment
import com.cleannote.notedetail.view.NoteDetailViewFragment
import com.cleannote.notelist.NoteListFragment
import com.cleannote.splash.SplashFragment
import javax.inject.Inject

class NoteFragmentFactory @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val sharedPreferences: SharedPreferences,
    private val glideReqManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {

        NoteListFragment::class.java.name -> {
            val fragment = NoteListFragment(viewModelFactory, sharedPreferences)
            fragment
        }

        NoteDetailViewFragment::class.java.name -> {
            val fragment = NoteDetailViewFragment(viewModelFactory, glideReqManager)
            fragment
        }

        NoteDetailEditFragment::class.java.name -> {
            val fragment = NoteDetailEditFragment(viewModelFactory, dateUtil, glideReqManager)
            fragment
        }

        SplashFragment::class.java.name -> {
            val fragment = SplashFragment(viewModelFactory)
            fragment
        }

        else -> super.instantiate(classLoader, className)
    }
}
