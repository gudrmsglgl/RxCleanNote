package com.cleannote.notelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.common.InputCaptureCallback
import com.cleannote.data.ui.InputType
import com.cleannote.mapper.NoteMapper
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.common.DateUtil
import com.cleannote.notedetail.NOTE_DETAIL_BUNDLE_KEY
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

    private val bundle: Bundle = Bundle()

    private val viewModel: NoteListViewModel by viewModels { viewModelFactory }
    private var noteAdapter: NoteListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeNoteList()
        insertNoteOnFab()
        subscribeInsertResult()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchNotes()
    }

    private fun initRecyclerView(){
        recycler_view.apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            noteAdapter = NoteListAdapter()
            adapter = noteAdapter
        }
    }

    private fun insertNoteOnFab(){
        add_new_note_fab
            .singleClick()
            .subscribe {
                showInputDialog(
                    getString(R.string.dialog_newnote),
                    InputType.NewNote,
                    object : InputCaptureCallback{
                        override fun onTextCaptured(text: String) {
                            /*val noteView = noteMapper.mapFromTitle(text)
                            val noteUiModel = noteMapper.mapToUiModel(noteView)
                            bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
                            viewModel.insertNotes(noteView)*/
                            val noteView = noteMapper.mapFromTitle(text)
                            val noteUiModel = noteMapper.mapToUiModel(noteView)
                            bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
                            findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment, bundle)
                        }
                    })}
            .addCompositeDisposable()
    }

    private fun subscribeNoteList() = viewModel.noteList.observe(viewLifecycleOwner,
        Observer { dataState ->
            if ( dataState != null ){

                when (dataState.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        showLoadingProgressBar(false)
                        fetchNotesToAdapter(dataState.data!!)
                    }
                    ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(dataState.message!!)
                    }
                }

            }
        })

    private fun fetchNotesToAdapter(notes: List<NoteView>) = notes
        .map { noteMapper.mapToUiModel(it) }
        .run { noteAdapter?.submitList(this) }

    private fun subscribeInsertResult() = viewModel.insertResult.observe(viewLifecycleOwner,
        Observer { dataState ->
            if (dataState != null){
                when (dataState.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        showLoadingProgressBar(false)
                        timber("d", "insert success: ${dataState.data}")
                        navDetailNote()
                    }
                    ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(dataState.message!!)
                    }
                }
            }
        })

    private fun navDetailNote(){
        findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment, bundle)
    }
}
