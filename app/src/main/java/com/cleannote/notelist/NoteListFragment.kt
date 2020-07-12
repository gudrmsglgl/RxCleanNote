package com.cleannote.notelist

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.common.InputCaptureCallback
import com.cleannote.data.ui.InputType
import com.cleannote.mapper.NoteMapper
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.common.DateUtil
import com.cleannote.common.OnBackPressListener
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_note_list.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class NoteListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val noteMapper: NoteMapper,
    private val dateUtil: DateUtil,
    private val sharedPreferences: SharedPreferences
): BaseFragment(R.layout.fragment_note_list),
    OnBackPressListener, TouchAdapter {

    private val bundle: Bundle = Bundle()

    private val viewModel: NoteListViewModel by viewModels { viewModelFactory }
    lateinit var noteAdapter: NoteListAdapter
    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        onRefresh()
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
                    setupSearchViewToolbar()
                }
                is MultiSelectState -> {

                }
            }
        }
    )

    private fun noteClick() = noteAdapter.clickNoteSubject
        .doOnNext { timber("d", "$it") }
        .subscribe {
            if (!it.isShowMenu){
                navDetailNote(it)
            }
            else
                showToast("${it.title} delete")
        }
        .addCompositeDisposable()

    private fun noteLongClick() = noteAdapter.longClickNoteSubject
        .doOnNext { timber("d", "$it") }
        .subscribe{
            noteAdapter.transItemMenu(it)
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
            itemTouchHelper = ItemTouchHelper(
                NoteItemTouchHelperCallback(
                    this@NoteListFragment,
                    ColorDrawable(resources.getColor(R.color.colorPrimaryDark))
                )
            )
            adapter = noteAdapter
            itemTouchHelper.attachToRecyclerView(this)
            scrollEvents()
                .filter { it.dy > 0 }
                .map {
                    (it.view.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() ==
                    it.view.adapter?.itemCount?.minus(1)
                }
                .filter { isLastPosition -> isLastPosition }
                .subscribe { viewModel.nextPage() }
                .addCompositeDisposable()

        }
    }

    private fun insertNoteOnFab() = add_new_note_fab.singleClick().subscribe {
        showInputDialog(
            getString(R.string.dialog_newnote),
            InputType.NewNote,
            object : InputCaptureCallback{
                override fun onTextCaptured(text: String) {
                    val noteView = noteMapper.mapFromTitle(text)
                    viewModel.insertNotes(noteView)
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

    private fun fetchNotesToAdapter(notes: List<NoteView>) {
        timber("d", "notes size: ${notes.size}")
        if (notes.isNotEmpty()){
            val noteUiModels = notes.map { noteMapper.mapToUiModel(it) }
            noteAdapter.submitList(noteUiModels)
        }
    }

    private fun subscribeInsertResult() = viewModel.insertResult
        .observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null){
                when (dataState.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        showLoadingProgressBar(false)
                        val noteUiModel = noteMapper.mapToUiModel(dataState.data!!)
                        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
                        findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment, bundle)
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
        toolbar_content_container
            .addView(
                View.inflate(it.context, R.layout.layout_searchview_toolbar, null)
                    .apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
            )
    }

    private fun setupSearchViewToolbar() = toolbar_content_container
        .findViewById<Toolbar>(R.id.searchview_toolbar)
        .apply {
            subscribeSearchView()
            subscribeFilterView()
        }

    private fun Toolbar.subscribeSearchView() = findViewById<SearchView>(R.id.search_view)
        .apply {
            queryTextChangeEvents()
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe {
                    with(viewModel){
                        searchKeyword(it.queryText.toString())
                        searchNotes()
                    }
                }
                .addCompositeDisposable()
        }

    private fun Toolbar.subscribeFilterView() = findViewById<ImageView>(R.id.action_filter)
        .singleClick()
        .subscribe {
            showFilterDialog()
        }
        .addCompositeDisposable()

    private fun showFilterDialog() = activity?.let {
        MaterialDialog(it).show {
            customView(R.layout.layout_filter)
            cancelable(true)

            @IdRes val cacheRadioBtn =
                if (ORDER_DESC == sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC))
                    R.id.radio_btn_desc
                else
                    R.id.radio_btn_asc

            val view = getCustomView()
            val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)
            radioGroup.check(cacheRadioBtn)
            val filterOk = view.findViewById<Button>(R.id.filter_btn_ok)

            val source = Observable.combineLatest(
                radioGroup.checkedChanges(),
                filterOk.singleClick(),
                BiFunction { resRadioBtn: Int, _: Unit ->
                    resRadioBtn
                })
                .subscribe { resId ->
                    if (resId == R.id.radio_btn_desc)
                        viewModel.orderingDESC()
                    else
                        viewModel.orderingASC()
                    viewModel.searchNotes()
                    dismiss()
                }

            onDismiss { source.dispose() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycler_view.adapter = null
    }

    override fun onSwiped(position: Int) {
        noteAdapter.transItemMenu(position)
    }

    private fun onRefresh() = swipe_refresh.setOnRefreshListener {
        swipe_refresh.isRefreshing = false
        noteAdapter.clearNotes()
        with(viewModel){
            clearQuery()
            searchNotes()
        }
    }

    override fun shouldBackPress(): Boolean {
        if (noteAdapter.isShowMenu()){
            noteAdapter.hideMenu()
            return false
        } else
            return true
    }

    override fun isSwiped(): Boolean = noteAdapter.isShowMenu()
}
