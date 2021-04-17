package com.cleannote.notelist

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.bumptech.glide.RequestManager
import com.cleannote.NoteApplication
import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteListBinding
import com.cleannote.common.*
import com.cleannote.common.dialog.DeleteDialog
import com.cleannote.common.dialog.InputDialog
import com.cleannote.extension.*
import com.cleannote.extension.rxbinding.*
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.IS_EXECUTE_INSERT
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_SCROLL_TOP_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.notelist.dialog.NoteDeleteDialog
import com.cleannote.notelist.swipe.SwipeAdapter
import com.cleannote.notelist.swipe.SwipeHelperCallback
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.recyclerview.scrollStateChanges
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlinx.android.synthetic.main.layout_search_toolbar.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    internal val sharedPref: SharedPreferences
) : BaseFragment<FragmentNoteListBinding>(R.layout.fragment_note_list),
    OnBackPressListener,
    SwipeAdapter {

    private val bundle: Bundle = Bundle()

    @Inject lateinit var glideReqManager: RequestManager
    @Inject lateinit var subjectManager: SubjectManager

    internal val viewModel: NoteListViewModel
        by navGraphViewModels(R.id.nav_app_graph) { viewModelFactory }
    private val tbFactory: NoteListTbFactory by lazy { NoteListTbFactory(this) }
    internal var noteAdapter by autoCleared<NoteListAdapter>()
    private var swipeHelperCallback by autoCleared<SwipeHelperCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        setStatusBarTextBlack()
        initRecyclerView()
        scrollEventNextPageSource()
        scrollEventCloseDeleteMenu()
        onRefresh()
        subscribeToolbarState()
        subscribeNoteList()
        createNote()
        subscribeInsertResult()
        subscribeDeleteResult()
        noteOnClick()
        noteOnCheck()
        noteOnLongClick()
        noteSwipeMenuOnDeleteClick()
        setFragmentResultListener(REQUEST_KEY_ON_BACK) { _, bundle ->
            requestUpdate(bundle)
            requestDelete(bundle)
            requestScrollTop(bundle)
        }
    }

    private fun initBinding() {
        binding.vm = viewModel
    }

    private fun subscribeToolbarState() = viewModel
        .toolbarState
        .observe(
            viewLifecycleOwner,
            Observer { toolbarState ->
                when (toolbarState) {
                    is SearchState -> {
                        tbFactory.addSearchViewToolbarContainer()
                        noteAdapter.changeAllNoteMode(Default)
                        noteAdapter.deleteCheckClear()
                    }
                    is MultiSelectState -> {
                        tbFactory.addMultiDeleteToolbarContainer()
                        noteAdapter.changeAllNoteMode(MultiDefault)
                    }
                }
            }
        )

    private fun noteOnClick() = noteAdapter
        .subjectManager
        .clickNoteSubject
        .filter {
            !swipeDeleteMenuClose()
        }
        .subscribe {
            navDetailNote(it, isInsertExecute = false)
        }
        .addCompositeDisposable()

    private fun noteOnCheck() = noteAdapter
        .subjectManager
        .checkNoteSubject
        .subscribe { (note, isChecked) ->
            if (isChecked)
                noteAdapter.deleteChecked(note)
            else
                noteAdapter.deleteNotChecked(note)
        }
        .addCompositeDisposable()

    private fun noteOnLongClick() = noteAdapter
        .subjectManager
        .longClickSubject
        .subscribe {
            viewModel.setToolbarState(MultiSelectState)
        }
        .addCompositeDisposable()

    private fun noteSwipeMenuOnDeleteClick() = noteAdapter
        .subjectManager
        .deleteClickSubject
        .filter { swipeHelperCallback.isVisibleDeleteMenu() }
        .subscribe {
            showDeleteDialog(listOf(it))
        }
        .addCompositeDisposable()

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() = binding
        .recyclerView
        .apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            setHasFixedSize(true)
            assignNoteAdapter()
            assignSwipeCallback()

            ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(this)

            adapter = noteAdapter

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }

    private fun scrollEventNextPageSource() = binding
        .recyclerView
        .scrollEvents()
        .filter { it.lastVisibleItemPos() == it.shouldNextPagePos() && viewModel.isNextPageExist }
        .throttleFirst(130L, TimeUnit.MILLISECONDS)
        .subscribe {
            viewModel.nextPage()
        }
        .addCompositeDisposable()

    private fun scrollEventCloseDeleteMenu() = binding
        .recyclerView
        .scrollStateChanges()
        .filter { swipeHelperCallback.isVisibleDeleteMenu() }
        .subscribe { swipeDeleteMenuClose() }
        .addCompositeDisposable()

    private fun createNote() = binding
        .addNewNoteFab
        .singleClick()
        .subscribe {
            showInputDialog()
        }
        .addCompositeDisposable()

    private fun showInputDialog() = activity?.let { context ->
        InputDialog(context, viewLifecycleOwner)
            .setHint(getString(R.string.dialog_new_note_hint))
            .setMessage(getString(R.string.dialog_new_note))
            .onPositiveClick {
                if (it != null)
                    viewModel.insertNotes(it.transNoteView())
            }
    }

    private fun subscribeNoteList() = viewModel
        .noteList
        .observe(
            viewLifecycleOwner,
            Observer { dataState ->
                if (dataState != null) {
                    showLoadingProgressBar(dataState.isLoading)
                    when (dataState.status) {
                        SUCCESS -> {
                            noteAdapter.fetchRecyclerView(currentNoteMode(dataState.data!!))
                        }
                        ERROR -> {
                            showErrorDialog(getString(R.string.searchErrorMsg))
                            dataState.sendFirebaseThrowable()
                        }
                    }
                }
            }
        )

    private fun subscribeInsertResult() = viewModel
        .insertResult
        .observe(
            viewLifecycleOwner,
            Observer { dataState ->
                if (dataState != null) {
                    showLoadingProgressBar(dataState.isLoading)
                    when (dataState.status) {
                        SUCCESS -> {
                            navDetailNote(dataState.data!!.transNoteUiModel(), isInsertExecute = true)
                        }
                        ERROR -> {
                            showErrorDialog(getString(R.string.insertErrorMsg))
                            dataState.sendFirebaseThrowable()
                        }
                    }
                }
            }
        )

    private fun subscribeDeleteResult() = viewModel
        .deleteResult
        .observe(
            viewLifecycleOwner,
            Observer { dataState ->
                if (dataState != null) {
                    showLoadingProgressBar(dataState.isLoading)
                    when (dataState.status) {
                        SUCCESS -> {
                            noteAdapter.deleteCheckClear()
                            transSearchState()
                            swipeDeleteMenuClose()
                            showToast(getString(R.string.deleteSuccessMsg))
                        }
                        ERROR -> {
                            noteAdapter.deleteCheckClear()
                            transSearchState()
                            swipeDeleteMenuClose()
                            showErrorDialog(getString(R.string.deleteErrorMsg))
                            dataState.sendFirebaseThrowable()
                        }
                    }
                }
            }
        )

    private fun navDetailNote(
        noteUiModel: NoteUiModel,
        isInsertExecute: Boolean
    ) {
        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
        bundle.putBoolean(IS_EXECUTE_INSERT, isInsertExecute)
        view?.clearFocus()
        findNavController().navigate(R.id.action_noteList_to_detail_nav_graph, bundle)
    }

    fun showDeleteDialog(param: List<NoteUiModel>) {
        if (param.isEmpty())
            showToast(getString(R.string.delete_multi_select_empty))
        else {
            activity?.let {
                NoteDeleteDialog(DeleteDialog(it, viewLifecycleOwner))
                    .showDialog(param)
                    .positiveButton {
                        deleteNotes(param)
                    }
                    .negativeButton {
                        transSearchState()
                        swipeDeleteMenuClose()
                    }
            }
        }
    }

    private fun deleteNotes(param: List<NoteUiModel>) {
        when {
            param.size == 1 -> viewModel.deleteNote(param[0].transNoteView())
            param.size > 1 -> viewModel.deleteMultiNotes(param.transNoteViews())
            else -> showToast(getString(R.string.delete_multi_select_empty))
        }
    }

    private fun requestUpdate(bundle: Bundle) {
        bundle.getParcelable<NoteUiModel>(REQ_UPDATE_KEY)?.let {
            viewModel.reqUpdateFromDetailFragment(it.transNoteView())
            scrollTop()
        }
    }

    private fun requestDelete(bundle: Bundle) {
        bundle.getParcelable<NoteUiModel>(REQ_DELETE_KEY)?.let {
            viewModel.reqDeleteFromDetailFragment(it.transNoteView())
        }
    }

    private fun requestScrollTop(bundle: Bundle) {
        val shouldScrollTop = bundle.getBoolean(REQ_SCROLL_TOP_KEY)
        if (shouldScrollTop)
            scrollTop()
    }

    private fun onRefresh() = binding.swipeRefresh.setOnRefreshListener {
        binding.swipeRefresh.isRefreshing = false
        tbFactory.setupSearchViewToolbarInit()
        viewModel.initNotes()
    }

    fun transSearchState() {
        if (curToolbarState() != SearchState)
            viewModel.setToolbarState(SearchState)
    }

    internal fun scrollTop() {
        Handler().postDelayed(
            {
                binding.recyclerView.scrollToPosition(0)
            },
            100L
        )
    }

    private fun assignNoteAdapter() = NoteListAdapter(
        this.requireContext(),
        glideReqManager,
        subjectManager
    ).also {
        it.setHasStableIds(true)
        noteAdapter = it
    }

    private fun assignSwipeCallback() = SwipeHelperCallback(
        clamp = this.requireContext().resources.getDimension(R.dimen.swipe_delete_clamp),
        extendClamp = this.requireContext().resources.getDimension(R.dimen.swipe_clamp_extend)
    ).also {
        it.setSwipeAdapter(this@NoteListFragment)
        swipeHelperCallback = it
    }

    private fun swipeDeleteMenuClose() = swipeHelperCallback.previousDeleteMenuClose(binding.recyclerView)

    private fun getNoteMode() = if (curToolbarState() == MultiSelectState) MultiDefault else Default

    private fun currentNoteMode(data: List<NoteView>) = data.transNoteUiModels(getNoteMode())

    private fun curToolbarState() = viewModel.toolbarState.value

    private fun inject() = activity?.let {
        (it.application as NoteApplication).applicationComponent.inject(this)
    }

    override fun isSwipeEnabled(): Boolean = noteAdapter.isDefaultMode()

    override fun shouldBackPress(): Boolean = if (!noteAdapter.isDefaultMode()) {
        transSearchState()
        false
    } else
        true

    override fun onDestroyView() {
        tbFactory.rxDispose()
        super.onDestroyView()
    }
}
