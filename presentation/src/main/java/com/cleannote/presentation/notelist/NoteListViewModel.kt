package com.cleannote.presentation.notelist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
import com.cleannote.domain.model.Note
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.SingleLiveEvent
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notelist.ListToolbarState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.extensions.transNote
import com.cleannote.presentation.extensions.transNoteViews
import com.cleannote.presentation.extensions.transNotes
import com.cleannote.presentation.model.NoteView

class NoteListViewModel
constructor(
    private val useCases: NoteListUseCases,
    internal val queryMgr: QueryManager
) : ViewModel() {

    val queryLike get() = queryMgr.queryLike()

    private val _toolbarState: MutableLiveData<ListToolbarState> = MutableLiveData(SearchState)
    val toolbarState: LiveData<ListToolbarState>
        get() = _toolbarState

    private val totalLoadNotes = mutableListOf<NoteView>()
    private val _mediatorNoteList: MediatorLiveData<DataState<List<NoteView>>> = MediatorLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _mediatorNoteList

    val isNextPageExist: Boolean
        get() = queryMgr.isNextPageExist

    private val _insertNote: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val insertResult: SingleLiveEvent<DataState<NoteView>>
        get() = _insertNote

    private val _deleteResult: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val deleteResult: SingleLiveEvent<DataState<NoteView>>
        get() = _deleteResult

    private val _mulDeleteResult: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val mulDeleteResult: SingleLiveEvent<DataState<NoteView>>
        get() = _mulDeleteResult


    init {
        with(_mediatorNoteList) {
            addSource(_insertNote) {
                if (it.status is SUCCESS) initNotes()
            }
            addSource(queryMgr.query) {
                searchNotes()
            }
        }
    }

    override fun onCleared() {
        _mediatorNoteList.removeSource(_insertNote)
        _mediatorNoteList.removeSource(queryMgr.query)
        queryMgr.dispose()
        useCases.disposeUseCases()
    }

    @VisibleForTesting
    fun searchNotes() {
        _mediatorNoteList.postValue(DataState.loading())
        useCases.searchNotes.execute(
            onSuccess = { loadNotes ->
                totalLoadNotes.addAllUpdate(loadNotes, isBackground = true)
                updateNextPageExist()
            },
            onError = {
                _mediatorNoteList.postValue(DataState.error(it))
            },
            params = queryMgr.getQuery()
        )
    }

    fun insertNotes(param: NoteView) {
        _insertNote.postValue(DataState.loading())
        useCases.insertNewNote.execute(
            onSuccess = {
                _insertNote.postValue(DataState.success(param))
            },
            onError = {
                _insertNote.postValue(DataState.error(it))
            },
            params = param.transNote()
        )
    }

    fun reqUpdateFromDetailFragment(param: NoteView) = when (queryMgr.cacheOrdering()) {
        ORDER_DESC -> totalLoadNotes.fetchFirstPositionUpdate(param)
        else -> initNotes()
    }

    fun reqDeleteFromDetailFragment(param: NoteView) {
        updateNextPageExist(param)
        totalLoadNotes.deleteSync(param, isBackground = false)
    }

    fun deleteNote(param: NoteView) {
        _deleteResult.postValue(DataState.loading())
        useCases.deleteNote.execute(
            onSuccess = {},
            onError = {
                _deleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _deleteResult.postValue(DataState.success(param))
                updateNextPageExist(param)
                totalLoadNotes.deleteSync(param, isBackground = true)
            },
            params = param.transNote()
        )
    }

    fun deleteMultiNotes(paramNotes: List<NoteView>) {
        _mulDeleteResult.postValue(DataState.loading())
        useCases.deleteMultipleNotes.execute(
            onSuccess = {},
            onError = {
                _mulDeleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _mulDeleteResult.postValue(DataState.success(null))
                initNotes()
            },
            params = paramNotes.transNotes()
        )
    }

    fun setOrdering(ordering: String) {
        totalLoadNotes.clear()
        queryMgr.resortingByOrder(ordering)
    }

    fun searchKeyword(search: String) {
        totalLoadNotes.clear()
        queryMgr.resetSearchQuery(search)
    }

    fun nextPage() {
        if (!isProcessSearch())
            queryMgr.updateNextPage()
    }

    fun setToolbarState(toolbarState: ListToolbarState) {
        _toolbarState.value = toolbarState
    }

    private fun updateNextPageExist(param: NoteView) = with(queryMgr) {
        executeNextPageExist(
            nextPageQuery(
                nextPage = totalLoadNotes.findPage(param) + 1,
                startIndex = totalLoadNotes.indexOf(param) % queryMgr.getQuery().limit
            )
        )
    }

    @VisibleForTesting
    fun updateNextPageExist() = with(queryMgr) {
        executeNextPageExist(
            nextPageQuery()
        )
    }

    private fun List<NoteView>.setNoteListSuccessState(isBackground: Boolean) {
        if (isBackground)
            _mediatorNoteList.postValue(DataState.success(this))
        else
            _mediatorNoteList.value = DataState.success(this)
    }

    private fun MutableList<NoteView>.addAllUpdate(
        param: List<Note>,
        isBackground: Boolean
    ) {
        addAll(param.transNoteViews())
        setNoteListSuccessState(isBackground)
    }

    private fun MutableList<NoteView>.fetchFirstPositionUpdate(param: NoteView) {
        val findNoteView = find { it.id == param.id }
        remove(findNoteView)
        add(0, param)
        setNoteListSuccessState(isBackground = false)
    }

    private fun MutableList<NoteView>.deleteSync(
        target: NoteView,
        isBackground: Boolean
    ) = if (isNextPageExist)
        deleteSyncCacheNotes(target)
    else
        deleteUpdate(target, isBackground)

    private fun MutableList<NoteView>.deleteUpdate(
        target: NoteView,
        isBackground: Boolean
    ) {
        remove(target)
        setNoteListSuccessState(isBackground)
    }

    private fun MutableList<NoteView>.deleteSyncCacheNotes(target: NoteView) {
        val deletedPage = findPage(target)
        val deletedIndex = indexOf(target) % queryMgr.getQuery().limit
        removeAll(takeTargetToLast(target))
        queryMgr.resetPageWithIndex(deletedPage, deletedIndex)
    }

    private fun List<NoteView>.takeTargetToLast(target: NoteView): List<NoteView> {
        val fromLastToDeleteIndex = (lastIndex - indexOf(target)).plus(1)
        return this.takeLast(fromLastToDeleteIndex)
    }

    private fun List<NoteView>.findPage(target: NoteView): Int {
        return (indexOf(target) / queryMgr.getQuery().limit) + 1
    }

    fun initNotes() {
        totalLoadNotes.clear()
        queryMgr.clearQuery() 
    }

    private fun isProcessSearch(): Boolean = noteList.value?.status == State.LOADING
}
