package com.cleannote.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.NoteListInteractors
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.notelist.NoteListViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject constructor(
    //private val noteListInteractors: NoteListInteractors,
    private val getNumNotes: GetNumNotes,
    private val noteMapper: NoteMapper
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){

        NoteListViewModel::class.java -> {
            NoteListViewModel(
                getNumNotes,
                noteMapper
            ) as T
        }

        else -> {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}