package com.cleannote.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.cleannote.notedetail.NoteDetailFragment
import com.cleannote.notelist.NoteListFragment
import com.cleannote.presentation.ViewModelFactory
import com.cleannote.presentation.util.DateUtil
import com.cleannote.splash.SplashFragment
import javax.inject.Inject

class NoteFragmentFactory @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when(className){

        NoteListFragment::class.java.name -> {
            val fragment = NoteListFragment(viewModelFactory, dateUtil)
            fragment
        }

        NoteDetailFragment::class.java.name -> {
            val fragment = NoteDetailFragment(viewModelFactory)
            fragment
        }

        SplashFragment::class.java.name -> {
            val fragment = SplashFragment(viewModelFactory)
            fragment
        }

        else -> super.instantiate(classLoader, className)
    }

}