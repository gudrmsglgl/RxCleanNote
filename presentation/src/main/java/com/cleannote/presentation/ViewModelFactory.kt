package com.cleannote.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleannote.domain.interactor.usecases.notelist.NoteListInteractors
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.note.NoteListViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory
@Inject constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteMapper: NoteMapper
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){

        NoteListViewModel::class.java -> {
            NoteListViewModel(
                noteListInteractors,
                noteMapper
            ) as T
        }

        else -> {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
    }
}