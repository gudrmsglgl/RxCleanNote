package com.cleannote.notelist

import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
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
import com.bumptech.glide.RequestManager

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
import com.cleannote.extension.transNoteUiModel
import com.cleannote.extension.transNoteUiModels
import com.cleannote.extension.transNoteView
import com.cleannote.extension.transNoteViews
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.Keys.REQUEST_KEY_ON_BACK
import com.cleannote.notedetail.Keys.REQ_DELETE_KEY
import com.cleannote.notedetail.Keys.REQ_UPDATE_KEY
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
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
    private val glideReqManager: RequestManager,
    private val sharedPreferences: SharedPreferences
): BaseFragment<FragmentNoteListBinding>(R.layout.fragment_note_list),
    OnBackPressListener, TouchAdapter {

    private val bundle: Bundle = Bundle()

    private val viewModel: NoteListViewModel by viewModels { viewModelFactory }
    lateinit var noteAdapter: NoteListAdapter
    lateinit var itemTouchHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        setStatusBarTextBlack()
        initRecyclerView()
        scrollEventNextPageSource()
        onRefresh()
        subscribeToolbarState()
        subscribeNoteList()
        insertNoteOnFab()
        subscribeInsertResult()
        subscribeDeleteResult()
        noteClick()
        setFragmentResultListener(REQUEST_KEY_ON_BACK){ _, bundle ->
            requestUpdate(bundle)
            requestDelete(bundle)
        }
    }

    override fun initBinding() {
        binding.vm = viewModel
    }

    private fun subscribeToolbarState() = viewModel.toolbarState.observe(viewLifecycleOwner,
        Observer { toolbarState ->
            when(toolbarState){
                is SearchState -> {
                    addSearchViewToolbarContainer()
                }
                is MultiSelectState -> {
                    addMultiDeleteToolbarContainer()
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

    private fun initRecyclerView(){
        recycler_view.apply {
            addItemDecoration(TopSpacingItemDecoration(20))
            setHasFixedSize(true)
            noteAdapter = NoteListAdapter(context, glideReqManager, viewModel).apply {
                setHasStableIds(true)
            }
            itemTouchHelper = ItemTouchHelper(
                NoteItemTouchHelperCallback(
                    this@NoteListFragment,
                    ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                )
            )
            adapter = noteAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun scrollEventNextPageSource() = binding.recyclerView
        .scrollEvents()
        .map {
            val lastVisibleItemPos = (it.view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val itemCount = it.view.adapter?.itemCount?.minus(1)
            mapOf(LAST_VISIBLE_ITEM_POS to lastVisibleItemPos, ITEM_COUNT to itemCount)
        }
        .filter {
            it[LAST_VISIBLE_ITEM_POS] == it[ITEM_COUNT] && viewModel.isExistNextPage()
        }
        .subscribe {
            viewModel.nextPage()
        }
        .addCompositeDisposable()

    private fun insertNoteOnFab() = add_new_note_fab.singleClick().subscribe {
        showInputDialog(
            getString(R.string.dialog_newnote),
            InputType.NewNote,
            object : InputCaptureCallback{
                override fun onTextCaptured(text: String) {
                    viewModel.insertNotes(text.transNoteView())
                }
            })}
        .addCompositeDisposable()

    private fun subscribeNoteList() = viewModel.noteList.observe(viewLifecycleOwner,
        Observer { dataState ->
            if ( dataState != null ){
                showLoadingProgressBar(dataState.isLoading)
                when (dataState.status) {
                    SUCCESS -> {
                        noteAdapter.submitList(
                            dataState.data!!.transNoteUiModels(getNoteMode()))
                    }
                    ERROR -> {
                        showErrorMessage(getString(R.string.searchErrorMsg))
                        dataState.sendFirebaseThrowable()
                    }
                    else -> {}
                }
            }
        })

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
                    else -> {}
                }
            }
        })

    private fun subscribeDeleteResult() = viewModel.deleteResult
        .observe( viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                showLoadingProgressBar(dataState.isLoading)
                when (dataState.status) {
                    SUCCESS -> {
                        transSearchState(shouldDefaultNoteMode = false)
                        showToast(getString(R.string.deleteSuccessMsg))
                    }
                    ERROR -> {
                        showErrorMessage(getString(R.string.deleteErrorMsg))
                        transSearchState(shouldDefaultNoteMode = true)
                        dataState.sendFirebaseThrowable()
                    }
                    else -> {}
                }
            }
        })

    private fun navDetailNote(noteUiModel: NoteUiModel){
        bundle.putParcelable(NOTE_DETAIL_BUNDLE_KEY, noteUiModel)
        findNavController().navigate(R.id.action_noteListFragment_to_noteDetailViewFragment, bundle)
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
        activity?.let {
            MaterialDialog(it).show {
                customView(R.layout.layout_filter)
                cancelable(true)

                val view = getCustomView()
                val filterOk = view.findViewById<Button>(R.id.filter_btn_ok)

                val setOrderSource = Observable.combineLatest(
                    filterRadioGroup(view).checkedChanges(),
                    filterOk.singleClick(),
                    BiFunction { selectedRadioBtn: Int, _: Unit ->
                        selectedRadioBtn
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
                        scrollTop()
                        dismiss()
                    }

                onDismiss { setOrderSource.dispose() }
            }
        }
    }

    private fun filterRadioGroup(view: View): RadioGroup {
        return view.findViewById<RadioGroup>(R.id.radio_group)
            .apply {
                check(getCachedRadioBtn())
            }
    }

    @IdRes
    private fun getCachedRadioBtn(): Int{
        return if (ORDER_DESC == sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC))
            R.id.radio_btn_desc
        else
            R.id.radio_btn_asc
    }

    fun showDeleteDialog(deleteMemos: List<NoteUiModel>) {
        activity?.let {
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
        when {
            deleteMemos.size == 1 -> viewModel.deleteNote(deleteMemos[0].transNoteView())
            deleteMemos.size > 1 -> viewModel.deleteMultiNotes(
                deleteMemos.transNoteViews()
            )
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

    override fun onDestroyView() {
        super.onDestroyView()
        recycler_view.adapter = null
    }

    override fun onSwiped(position: Int) {
        noteAdapter.changeNoteMode(SingleDelete, position)
    }

    private fun onRefresh() = swipe_refresh.setOnRefreshListener {
        swipe_refresh.isRefreshing = false
        viewModel.clearQuery()
    }

    override fun shouldBackPress(): Boolean {
        if (noteAdapter.isNotDefaultNote()){
            transSearchState(true)
            return false
        } else
            return true
    }

    override fun isSwipeEnable(): Boolean = noteAdapter.isSwipeMode()

    fun transSearchState(shouldDefaultNoteMode: Boolean = true){
        if (curToolbarState() != SearchState)
            viewModel.setToolbarState(SearchState)
        if (shouldDefaultNoteMode)
            noteAdapter.changeNoteMode(Default)
    }

    private fun scrollTop(){
        Handler().postDelayed({
            binding.recyclerView.scrollToPosition(0)
        }, 100L)
    }

    private fun getNoteMode() = if (curToolbarState() == MultiSelectState) MultiDefault else Default

    private fun curToolbarState() = viewModel.toolbarState.value

    companion object{
        const val LAST_VISIBLE_ITEM_POS = "lastVisibleItemPosKey"
        const val ITEM_COUNT = "itemCountKey"
    }

}
