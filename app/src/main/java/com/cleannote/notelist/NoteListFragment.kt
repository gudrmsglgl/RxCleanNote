    package com.cleannote.notelist

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.cleannote.NoteApplication

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteListBinding
import com.cleannote.app.databinding.LayoutMultideleteToolbarBinding
import com.cleannote.app.databinding.LayoutSearchviewToolbarBinding
import com.cleannote.common.BaseFragment
import com.cleannote.common.InputCaptureCallback
import com.cleannote.data.ui.InputType
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.notelist.NoteListViewModel
import com.cleannote.common.OnBackPressListener
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.extension.*
import com.cleannote.extension.rxbinding.itemCount
import com.cleannote.extension.rxbinding.lastVisibleItemPos
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.notelist.swipe.SwipeAdapter
import com.cleannote.notelist.swipe.SwipeHelperCallback
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import com.jakewharton.rxbinding4.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_note_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteListFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val sharedPref: SharedPreferences
): BaseFragment<FragmentNoteListBinding>(R.layout.fragment_note_list),
    OnBackPressListener,
    SwipeAdapter
{

    companion object{
        const val LAST_VISIBLE_ITEM_POS = "lastVisibleItemPosKey"
        const val ITEM_COUNT = "itemCountKey"
    }

    private val bundle: Bundle = Bundle()
    private val viewModel: NoteListViewModel by viewModels { viewModelFactory }

    @Inject lateinit var noteAdapter: NoteListAdapter
    @Inject lateinit var swipeHelperCallback: SwipeHelperCallback

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
        noteOnLongClick()
        noteSwipeMenuOnDeleteClick()
        noteOnMultiCheck()
        setFragmentResultListener(REQUEST_KEY_ON_BACK){ _, bundle ->
            requestUpdate(bundle)
            requestDelete(bundle)
        }
    }

    override fun initBinding() {
        binding.vm = viewModel
    }

    private fun subscribeToolbarState() = viewModel
        .toolbarState
        .observe(viewLifecycleOwner, Observer { toolbarState ->
            when(toolbarState){
                is SearchState -> {
                    addSearchViewToolbarContainer()
                    noteAdapter.changeNoteMode(Default)
                }
                is MultiSelectState -> {
                    addMultiDeleteToolbarContainer()
                    noteAdapter.deleteCheckClear()
                    noteAdapter.changeNoteMode(SelectMode)
                }
            }}
        )

    private fun noteOnClick() = noteAdapter
        .subjectManager
        .clickNoteSubject
        .filter {
            !swipeHelperCallback.previousDeleteMenuClose(binding.recyclerView)
        }
        .subscribe {
            if (it.mode == Default)
                navDetailNote(it)
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

    private fun noteOnMultiCheck() = noteAdapter
        .subjectManager
        .multiClickSubject
        .subscribe {
            checkDeleteNotes(it)
        }
        .addCompositeDisposable()

    private fun checkDeleteNotes(param: Pair<Int, Boolean>){
        val position = param.first
        val isChecked = param.second
        if (isChecked)
            noteAdapter.deleteChecked(position)
        else
            noteAdapter.deleteNotChecked(position)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() = binding
        .recyclerView
        .apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            setHasFixedSize(true)

            ItemTouchHelper(
                swipeHelperCallback.apply { setSwipeAdapter(this@NoteListFragment) }
            ).attachToRecyclerView(this)

            adapter = noteAdapter

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }

        }

    private fun scrollEventNextPageSource() = binding
        .recyclerView
        .scrollEvents()
        .map {
            mapOf(LAST_VISIBLE_ITEM_POS to it.lastVisibleItemPos(), ITEM_COUNT to it.itemCount())
        }
        .filter {
            it[LAST_VISIBLE_ITEM_POS] == it[ITEM_COUNT] && viewModel.isExistNextPage()
        }
        .subscribe {
            viewModel.nextPage()
        }
        .addCompositeDisposable()

    private fun scrollEventCloseDeleteMenu() = binding
        .recyclerView
        .scrollStateChanges()
        .subscribe {
            swipeHelperCallback.previousDeleteMenuClose(binding.recyclerView)
        }
        .addCompositeDisposable()

    private fun createNote() = binding
        .addNewNoteFab
        .singleClick()
        .subscribe {
            showNewNoteDialog {
                viewModel.insertNotes(it.transNoteView())
            }
        }
        .addCompositeDisposable()

    private inline fun showNewNoteDialog(crossinline func: (String) -> Unit){
        showInputDialog(
            getString(R.string.dialog_newnote),
            InputType.NewNote,
            object : InputCaptureCallback {
                override fun onTextCaptured(text: String) {
                    func.invoke(text)
                }
            }
        )
    }

    private fun subscribeNoteList() = viewModel
        .noteList
        .observe(viewLifecycleOwner, Observer { dataState ->
            if ( dataState != null ){
                showLoadingProgressBar(dataState.isLoading)
                when (dataState.status) {
                    SUCCESS -> {
                        noteAdapter
                            .submitList(dataState.data!!.transNoteUiModels(getNoteMode()))
                    }
                    ERROR -> {
                        showErrorMessage(getString(R.string.searchErrorMsg))
                        dataState.sendFirebaseThrowable()
                    }
                }
            }}
        )

    private fun subscribeInsertResult() = viewModel.insertResult
        .observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null){
                showLoadingProgressBar(dataState.isLoading)
                when (dataState.status) {
                    SUCCESS -> {
                        navDetailNote(dataState.data!!.transNoteUiModel())
                    }
                    ERROR -> {
                        showErrorMessage(getString(R.string.insertErrorMsg))
                        dataState.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun subscribeDeleteResult() = viewModel.deleteResult
        .observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                showLoadingProgressBar(dataState.isLoading)
                when (dataState.status) {
                    SUCCESS -> {
                        transSearchState()
                        showToast(getString(R.string.deleteSuccessMsg))
                    }
                    ERROR -> {
                        showErrorMessage(getString(R.string.deleteErrorMsg))
                        transSearchState()
                        dataState.sendFirebaseThrowable()
                    }
                }
            }
        })

    private fun navDetailNote(noteUiModel: NoteUiModel){
        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
        findNavController().navigate(R.id.action_noteList_to_detail_nav_graph, bundle)
    }

    private fun addSearchViewToolbarContainer() = view?.let {
        binding.toolbarContentContainer.apply {
            removeAllViews()
            addView(searchToolbarBinding(this).root)
        }
    }

    private fun searchToolbarBinding(parent: ViewGroup): LayoutSearchviewToolbarBinding {
        val binding: LayoutSearchviewToolbarBinding = bindingInflate(R.layout.layout_searchview_toolbar, parent)
        return binding.apply {
            fragment = this@NoteListFragment
            searchTextChangeEventSource(searchView)
        }
    }

    private fun searchTextChangeEventSource(searchView: SearchView){
        searchView.apply {
            queryTextChangeEvents()
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe { viewModel.searchKeyword(it.queryText.toString()) }
                .addCompositeDisposable()
        }
    }

    private fun addMultiDeleteToolbarContainer() = view?.let {
        binding.toolbarContentContainer.apply {
            removeAllViews()
            addView(multiDeleteToolbarBinding(this).root)
        }
    }

    private fun multiDeleteToolbarBinding(parent: ViewGroup): LayoutMultideleteToolbarBinding {
        val binding: LayoutMultideleteToolbarBinding = bindingInflate(R.layout.layout_multidelete_toolbar, parent)
        return binding.apply {
            fragment = this@NoteListFragment
            adapter = noteAdapter
        }
    }

    fun showFilterDialog() {
        activity?.let { activity ->
            MaterialDialog(activity).show {
                customView(R.layout.layout_filter)
                cancelable(true)

                val view = getCustomView()
                val filterOk = view.findViewById<Button>(R.id.filter_btn_ok)

                val setOrderSource = 
                    dialogOkClickSource(dialogView = view, okBtn = filterOk)
                    .subscribe { order ->
                        saveCacheThenOrdering(order)
                        scrollTop()
                        dismiss()
                    }

                onDismiss { setOrderSource.dispose() }
            }
        }
    }

    private fun dialogOkClickSource(
        dialogView: View,
        okBtn: View
    ) = Observable.combineLatest(
        selectedRadioBtnSource(dialogView),
        okBtn.singleClick(),
        BiFunction { selectedRadioBtn: Int, _: Unit ->
            selectedRadioBtn
        })
        .map {
            if (it == R.id.radio_btn_desc)
                ORDER_DESC
            else
                ORDER_ASC
        }

    private fun selectedRadioBtnSource(view: View) = initCheckedRadioGroup(view).checkedChanges()

    private fun initCheckedRadioGroup(view: View): RadioGroup {
        return view
            .findViewById<RadioGroup>(R.id.radio_group)
            .apply {
                check(getCachedRadioBtn())
            }
    }

    @IdRes
    private fun getCachedRadioBtn(): Int{
        return if (ORDER_DESC == sharedPref.getString(FILTER_ORDERING_KEY, ORDER_DESC))
            R.id.radio_btn_desc
        else
            R.id.radio_btn_asc
    }

    private fun saveCacheThenOrdering(order: String){
        sharedPref.edit().putString(FILTER_ORDERING_KEY, order).apply()
        viewModel.setOrdering(order)
    }

    fun showDeleteDialog(param: List<NoteUiModel>) {
        activity?.let {
            MaterialDialog(it).show {
                title(R.string.delete_title)
                message(text = deleteTitle(param))
                positiveButton(R.string.delete_ok){
                    deleteNotes(param)
                }
                negativeButton(R.string.delete_cancel){
                    showToast(getString(R.string.deleteCancelMsg))
                    transSearchState()
                    dismiss()
                }
                cancelable(false)
            }
        }
    }

    private fun deleteTitle(
        deleteMemos: List<NoteUiModel>
    ): String = when {
        deleteMemos.size == 1 -> {
            """
                |${deleteMemos[0].title}
                |메모를 삭제 하시겠습니까?
            """.trimMargin()
        }
        deleteMemos.size > 1 -> {
            getString(R.string.delete_multi_select_message)
        }
        else -> ""
    }

    private fun deleteNotes(param: List<NoteUiModel>){
        when {
            param.size == 1 -> viewModel.deleteNote(param[0].transNoteView())
            param.size > 1 -> viewModel.deleteMultiNotes(param.transNoteViews())
            else -> showToast(getString(R.string.delete_multi_select_empty))
        }
    }

    private fun requestUpdate(bundle: Bundle){
        bundle.getParcelable<NoteUiModel>(REQ_UPDATE_KEY)?.let {
            viewModel.reqUpdateFromDetailFragment(it.transNoteView())
        }
    }

    private fun requestDelete(bundle: Bundle){
        bundle.getParcelable<NoteUiModel>(REQ_DELETE_KEY)?.let {
            viewModel.reqDeleteFromDetailFragment(it.transNoteView())
        }
    }

    private fun onRefresh() = swipe_refresh.setOnRefreshListener {
        swipe_refresh.isRefreshing = false
        viewModel.clearQuery()
    }

    fun transSearchState(){
        if (curToolbarState() != SearchState)
            viewModel.setToolbarState(SearchState)
    }

    private fun scrollTop(){
        Handler().postDelayed({
            binding.recyclerView.scrollToPosition(0)
        }, 100L)
    }

    private fun getNoteMode() = if (curToolbarState() == MultiSelectState) SelectMode else Default

    private fun curToolbarState() = viewModel.toolbarState.value

    private fun inject() = activity?.let{
        (it.application as NoteApplication).applicationComponent.inject(this)
    }

    override fun isSwipeEnabled(): Boolean  = noteAdapter.isDefaultMode()

    override fun shouldBackPress(): Boolean = if (!noteAdapter.isDefaultMode()) {
        transSearchState()
        false
    } else
        true

}
