package com.cleannote.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.NoteDetailUseCases
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.presentation.splash.SplashViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject constructor(
    private val login: Login,
    private val noteListUseCases: NoteListUseCases,
    private val noteDetailUseCases: NoteDetailUseCases,
    private val sharedPreferences: SharedPreferences
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){

        SplashViewModel::class.java -> SplashViewModel(login) as T

        NoteListViewModel::class.java -> NoteListViewModel(noteListUseCases, sharedPreferences) as T

        NoteDetailViewModel::class.java -> NoteDetailViewModel(noteDetailUseCases) as T

        else -> {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}