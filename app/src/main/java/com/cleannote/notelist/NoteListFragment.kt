package com.cleannote.notelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.mapper.NoteMapper
import com.cleannote.presentation.data.State
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.presentation.util.DateUtil
import kotlinx.android.synthetic.main.fragment_note_list.*

/**
 * A simple [Fragment] subclass.
 */
class NoteListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val noteMapper: NoteMapper,
    private val dateUtil: DateUtil
): BaseFragment(R.layout.fragment_note_list) {

    private val viewModel: NoteListViewModel by viewModels { viewModelFactory }
    private var noteAdapter: NoteListAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        subscribeNoteList()
    }

    private fun initRecyclerView(){
        recycler_view.apply {

            noteAdapter = NoteListAdapter()

            adapter = noteAdapter
        }
    }

    private fun subscribeNoteList() = viewModel.noteList.observe(viewLifecycleOwner,
        Observer { dataState ->
            if ( dataState != null ){

                when (dataState.status) {
                    State.LOADING -> showLoadingProgressBar(true)
                    State.SUCCESS -> {
                        showLoadingProgressBar(false)
                        val noteUiModels = dataState.data!!.map {
                            noteMapper.mapToUiModel(it)
                        }
                        noteAdapter?.submitList(noteUiModels)
                    }
                    State.ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(dataState.message!!)
                    }
                }

            }
        })

}
