package com.cleannote.common

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.cleannote.mapper.UserMapper
import com.cleannote.notedetail.NoteDetailFragment
import com.cleannote.notelist.NoteListFragment
import com.cleannote.splash.SplashFragment
import javax.inject.Inject

class NoteFragmentFactory @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val userMapper: UserMapper,
    private val sharedPreferences: SharedPreferences,
    private val glideReqManager: RequestManager
): FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when(className){

        NoteListFragment::class.java.name -> {
            val fragment = NoteListFragment(viewModelFactory, glideReqManager, sharedPreferences)
            fragment
        }

        NoteDetailFragment::class.java.name -> {
            val fragment = NoteDetailFragment(viewModelFactory, dateUtil, glideReqManager)
            fragment
        }

        SplashFragment::class.java.name -> {
            val fragment = SplashFragment(viewModelFactory, userMapper)
            fragment
        }

        else -> super.instantiate(classLoader, className)
    }

}