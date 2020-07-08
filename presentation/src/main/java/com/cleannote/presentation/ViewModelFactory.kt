package com.cleannote.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.mapper.UserMapper
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.presentation.splash.SplashViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject constructor(
    private val login: Login,
    private val userMapper: UserMapper,
    private val getNumNotes: GetNumNotes,
    private val searchNotes: SearchNotes,
    private val insertNewNote: InsertNewNote,
    private val noteMapper: NoteMapper
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){

        SplashViewModel::class.java -> SplashViewModel(login, userMapper) as T

        NoteListViewModel::class.java -> NoteListViewModel(
            getNumNotes, searchNotes, insertNewNote, noteMapper) as T

        NoteDetailViewModel::class.java -> NoteDetailViewModel() as T

        else -> {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}