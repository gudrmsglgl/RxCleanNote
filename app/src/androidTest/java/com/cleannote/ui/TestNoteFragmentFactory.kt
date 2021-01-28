package com.cleannote.ui

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.cleannote.common.DateUtil
import com.cleannote.common.UIController
import com.cleannote.notedetail.edit.NoteDetailFragment
import com.cleannote.notelist.NoteListFragment
import com.cleannote.splash.SplashFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestNoteFragmentFactory
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil,
    private val sharedPreferences: SharedPreferences,
    private val glideReqManager: RequestManager
): FragmentFactory() {

    lateinit var uiController: UIController

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when(className){

        NoteListFragment::class.java.name -> {
            val fragment = NoteListFragment(viewModelFactory, sharedPreferences)
            if (this::uiController.isInitialized){
                fragment.setUIController(uiController)
            }
            fragment
        }

        NoteDetailFragment::class.java.name -> {
            val fragment = NoteDetailFragment(viewModelFactory, dateUtil, glideReqManager)
            if (this::uiController.isInitialized){
                fragment.setUIController(uiController)
            }
            fragment
        }

        SplashFragment::class.java.name -> {
            val fragment = SplashFragment(viewModelFactory)
            if (this::uiController.isInitialized){
                fragment.setUIController(uiController)
            }
            fragment
        }

        else -> super.instantiate(classLoader, className)
    }
}