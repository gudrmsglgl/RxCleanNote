package com.cleannote.notelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
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
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import kotlinx.android.synthetic.main.fragment_note_list.*
import java.util.concurrent.TimeUnit

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
    lateinit var noteAdapter: NoteListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        subscribeToolbar()
        subscribeNoteList()
        insertNoteOnFab()
        subscribeInsertResult()
        noteClick()
        noteLongClick()
    }

    private fun subscribeToolbar() = viewModel.toolbarState.observe(viewLifecycleOwner,
        Observer { toolbarState ->
            when(toolbarState){
                is SearchState -> {
                    addSearchViewToolbarContainer()
                    setupSearchView()
                }
                is MultiSelectState -> {

                }
            }
        }
    )

    private fun noteClick() = noteAdapter.clickNoteSubject
        .doOnNext { timber("d", "$it") }
        .subscribe { navDetailNote(it) }
        .addCompositeDisposable()

    private fun noteLongClick() = noteAdapter.longClickNoteSubject
        .doOnNext { timber("d", "$it") }
        .subscribe{

        }
        .addCompositeDisposable()

    override fun onResume() {
        super.onResume()
        viewModel.searchNotes()
    }

    private fun initRecyclerView(){
        recycler_view.apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            noteAdapter = NoteListAdapter()
            adapter = noteAdapter
        }
    }

    private fun insertNoteOnFab() = add_new_note_fab.singleClick().subscribe {
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
        .run { noteAdapter.submitList(this) }

    private fun subscribeInsertResult() = viewModel.insertResult.observe(viewLifecycleOwner,
        Observer { dataState ->
            if (dataState != null){
                when (dataState.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        showLoadingProgressBar(false)
                        timber("d", "insert success: ${dataState.data}")
                        // TODO navDetailNote
                    }
                    ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(dataState.message!!)
                    }
                }
            }
        })

    private fun navDetailNote(noteUiModel: NoteUiModel){
        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
        findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment, bundle)
    }

    private fun addSearchViewToolbarContainer() = view?.let {
        val searchView = View
            .inflate(it.context, R.layout.layout_searchview_toolbar, null)
            .apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
        toolbar_content_container.addView(searchView)
    }

    private fun setupSearchView() = toolbar_content_container
        .findViewById<Toolbar>(R.id.searchview_toolbar)
        .apply {
            findViewById<SearchView>(R.id.search_view)
                .apply {
                    queryTextChangeEvents()
                        .debounce(1000, TimeUnit.MILLISECONDS)
                        .subscribe {
                            with(viewModel){
                                searchKeyword(it.queryText.toString())
                                searchNotes()
                            }
                        }
                }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        recycler_view.adapter = null
    }
}
