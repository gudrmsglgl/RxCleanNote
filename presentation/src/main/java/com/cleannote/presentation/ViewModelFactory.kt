package com.cleannote.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleannote.domain.interactor.usecases.notedetail.NoteDetailUseCases
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.presentation.notelist.QueryManager
import com.cleannote.presentation.splash.SplashViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject constructor(
    private val login: Login,
    private val noteListUseCases: NoteListUseCases,
    private val noteDetailUseCases: NoteDetailUseCases,
    private val queryManager: QueryManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {

        SplashViewModel::class.java -> SplashViewModel(login) as T

        NoteListViewModel::class.java -> NoteListViewModel(noteListUseCases, queryManager) as T

        NoteDetailViewModel::class.java -> NoteDetailViewModel(noteDetailUseCases) as T

        else -> {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}
