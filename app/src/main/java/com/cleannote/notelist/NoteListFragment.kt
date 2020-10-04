package com.cleannote.notelist

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
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
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.common.OnBackPressListener
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.NOTE_DETAIL_DELETE_KEY
import com.cleannote.notedetail.REQUEST_KEY_ON_BACK
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.model.NoteView
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_note_list.*
import java.util.concurrent.TimeUnit
class NoteListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val noteMapper: NoteMapper,
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
        subscribeToolbarState()
        subscribeNoteList()
        insertNoteOnFab()
        subscribeInsertResult()
        subscribeDeleteResult()
        noteClick()
        noteLongClick()
        setFragmentResultListener(REQUEST_KEY_ON_BACK){ _, bundle ->
            requestUpdate(bundle)
            requestDelete(bundle)
        }
    }

    private fun subscribeToolbarState() = viewModel.toolbarState.observe(viewLifecycleOwner,
        Observer { toolbarState ->
            when(toolbarState){
                is SearchState -> {
                    addSearchViewToolbarContainer()
                    setupSearchViewToolbar()
                }
                is MultiSelectState -> {
                    addMultiDeleteToolbarContainer()
                    setupMultiDeleteToolbar()
                }
            }
        }
    )

    private fun noteClick() = noteAdapter.clickNoteSubject
        .doOnNext { timber("d", "$it") }
        .subscribe {
            if (it.mode == NoteMode.Default){
                navDetailNote(it)
            }
            else if (it.mode == NoteMode.SingleDelete){
                showDeleteDialog(listOf(it))
            }
        }
        .addCompositeDisposable()

    private fun noteLongClick() = noteAdapter.longClickNoteSubject
        .subscribe {
            transMultiDeleteState()
        }
        .addCompositeDisposable()

    private fun initRecyclerView(){
        recycler_view.apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            noteAdapter = NoteListAdapter(context)
            itemTouchHelper = ItemTouchHelper(
                NoteItemTouchHelperCallback(
                    this@NoteListFragment,
                    ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                )
            )
            adapter = noteAdapter
            itemTouchHelper.attachToRecyclerView(this)

            scrollEvents()
                /*.filter {
                    timber("d", "${it.dy}")
                    it.dy >= 0}*/
                .filter {
                    timber("d", "findLastCompletelyVisibleItemPosition: ${(it.view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()}")
                    (it.view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() ==
                    it.view.adapter?.itemCount?.minus(1)
                }
                .filter {
                    viewModel.isExistNextPage()
                }
                /*.filter { isLastPosition ->
                    timber("d","isLastPosition: $isLastPosition")
                    isLastPosition && viewModel.isExistNextPage()
                }*/
                .subscribe {
                    timber("d", "doOnSubscribe2")
                    viewModel.nextPage()
                }
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
                        showErrorMessage(getString(R.string.searchErrorMsg))
                        dataState.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun fetchNotesToAdapter(notes: List<NoteView>) {
        timber("d", "notes size: ${notes.size}")
        showEmptyData(notes.isEmpty())
        val noteUiModels = notes.map {
            if (viewModel.toolbarState.value == MultiSelectState)
                noteMapper.mapToUiModel(it).apply { mode = NoteMode.MultiDefault }
            else
                noteMapper.mapToUiModel(it)
        }
        noteAdapter.submitList(noteUiModels)
    }

    private fun showEmptyData(isEmptyData: Boolean) = if (isEmptyData) {
        recycler_view.visibility = GONE
        tv_no_data.visibility = VISIBLE
    } else {
        recycler_view.visibility = VISIBLE
        tv_no_data.visibility = GONE
    }

    private fun subscribeInsertResult() = viewModel.insertResult
        .observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null){
                when (dataState.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        showLoadingProgressBar(false)
                        val noteUiModel = noteMapper.mapToUiModel(dataState.data!!)
                        navDetailNote(noteUiModel)
                    }
                    ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(getString(R.string.insertErrorMsg))
                        dataState.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun subscribeDeleteResult() = viewModel.deleteResult
        .observe( viewLifecycleOwner, Observer {
            if (it != null) {
                when (it.status) {
                    LOADING -> showLoadingProgressBar(true)
                    SUCCESS -> {
                        transSearchState(isTransNotes = false)
                        showLoadingProgressBar(false)
                        showToast(getString(R.string.deleteSuccessMsg))
                    }
                    ERROR -> {
                        showLoadingProgressBar(false)
                        showErrorMessage(getString(R.string.deleteErrorMsg))
                        transSearchState(isTransNotes = true)
                        it.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun navDetailNote(noteUiModel: NoteUiModel){
        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
        findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment, bundle)
    }

    private fun addSearchViewToolbarContainer() = view?.let {
        toolbar_content_container.apply {
            removeAllViews()
            addView(
                View.inflate(it.context, R.layout.layout_searchview_toolbar, null)
                    .apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
            )
        }
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
                .subscribe { viewModel.searchKeyword(it.queryText.toString()) }
                .addCompositeDisposable()
        }

    private fun Toolbar.subscribeFilterView() = findViewById<ImageView>(R.id.action_filter)
        .singleClick()
        .subscribe {
            showFilterDialog()
        }
        .addCompositeDisposable()

    private fun addMultiDeleteToolbarContainer() = view?.let {
        toolbar_content_container.apply {
            removeAllViews()
            addView(
                View.inflate(it.context, R.layout.layout_multidelete_toolbar, null)
                    .apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
            )
        }
    }

    private fun setupMultiDeleteToolbar() = toolbar_content_container
        .findViewById<Toolbar>(R.id.toolbar_multi_delete)
        .apply{
            findViewById<ImageView>(R.id.btn_multi_delete_cancel).apply {
                singleClick()
                    .subscribe {
                        transSearchState()
                    }
                    .addCompositeDisposable()
            }
            findViewById<ImageView>(R.id.btn_multi_delete_ok).apply {
                singleClick()
                    .subscribe {
                        showDeleteDialog(noteAdapter.getMultiSelectedNotes())
                    }
                    .addCompositeDisposable()
            }
        }

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
                .map {
                    if (it == R.id.radio_btn_desc)
                        ORDER_DESC
                    else
                        ORDER_ASC
                }
                .subscribe { order ->
                    sharedPreferences.edit().putString(FILTER_ORDERING_KEY, order).apply()
                    viewModel.setOrdering(order)
                    dismiss()
                }

            onDismiss { source.dispose() }
        }
    }

    private fun showDeleteDialog(deleteMemos: List<NoteUiModel>) = activity?.let {
        MaterialDialog(it).show {
            title(R.string.delete_title)
            message(text = deleteTitle(deleteMemos))
            positiveButton(R.string.delete_ok){
                actionDelete(deleteMemos)
            }
            negativeButton(R.string.delete_cancel){
                showToast(getString(R.string.deleteCancelMsg))
                transSearchState()
                dismiss()
            }
            cancelable(false)
        }
    }

    private fun deleteTitle(deleteMemos: List<NoteUiModel>): String {
        return if (deleteMemos.size == 1) {
            """${deleteMemos[0].title}
                ${getString(R.string.delete_message)}
            """.trimIndent()
        } else {
            getString(R.string.delete_multi_select_message)
        }
    }

    private fun actionDelete(deleteMemos: List<NoteUiModel>){
        if (deleteMemos.size == 1)
            viewModel.deleteNote(noteMapper.mapToView(deleteMemos[0]))
        else if (deleteMemos.size > 1)
            viewModel.deleteMultiNotes(
                deleteMemos.map{ noteMapper.mapToView(it) }
            )
        else showToast(getString(R.string.delete_multi_select_empty))
    }

    private fun requestUpdate(bundle: Bundle){
        bundle.getParcelable<NoteUiModel>(NOTE_DETAIL_BUNDLE_KEY)?.let {
            viewModel.notifyUpdatedNote(noteMapper.mapToView(it))
        }
    }

    private fun requestDelete(bundle: Bundle){
        bundle.getParcelable<NoteUiModel>(NOTE_DETAIL_DELETE_KEY)?.let {
            viewModel.notifyDeletedNote(noteMapper.mapToView(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycler_view.adapter = null
    }

    override fun onSwiped(position: Int) {
        noteAdapter.transNoteSingleDelete(position)
    }

    private fun onRefresh() = swipe_refresh.setOnRefreshListener {
        swipe_refresh.isRefreshing = false
        viewModel.clearQuery()
    }

    override fun shouldBackPress(): Boolean {
        if (noteAdapter.isNotDefaultNote()){
            transSearchState()
            return false
        } else
            return true
    }

    override fun isSwipeEnable(): Boolean = !noteAdapter.isNotDefaultNote()

    private fun transSearchState(isTransNotes: Boolean = true){
        if (viewModel.toolbarState.value != SearchState)
            viewModel.setToolbarState(SearchState)
        if (isTransNotes)
            noteAdapter.transAllDefaultNote()
    }

    private fun transMultiDeleteState(isTransNotes: Boolean = true){
        viewModel.setToolbarState(MultiSelectState)
        if (isTransNotes)
            noteAdapter.transAllMultiSelectDefaultNote()
    }
}
