package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.SingleLiveEvent
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
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    private val _query: MutableLiveData<Query> = MutableLiveData(Query(
        order = loadOrderingOnSharedPref()
    ))
    val queryLike get() = _query.value?.like ?: ""

    private val _toolbarState: MutableLiveData<ListToolbarState> = MutableLiveData(SearchState)
    val toolbarState: LiveData<ListToolbarState>
        get() = _toolbarState

    private val totalLoadNotes = mutableListOf<NoteView>()
    private val _mediatorNoteList: MediatorLiveData<DataState<List<NoteView>>> = MediatorLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _mediatorNoteList

    /*private var _isLastNote = false
    val isLastNote get() = _isLastNote*/

    private var _isNextPageExist: MediatorLiveData<DataState<Boolean>> = MediatorLiveData()
    val isNextPageExist: Boolean
        get() = _isNextPageExist.value?.data ?: false

    private val _insertNote: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val insertResult: SingleLiveEvent<DataState<NoteView>>
        get() = _insertNote

    private val _deleteResult: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val deleteResult: SingleLiveEvent<DataState<NoteView>>
        get() = _deleteResult

    init {
        with(_mediatorNoteList) {
            addSource(_insertNote){
                if (it.status is SUCCESS) initNotes()
            }
            addSource(_query) {
                searchNotes()
            }
        }
    }

    override fun onCleared() {
        _mediatorNoteList.removeSource(_insertNote)
        _mediatorNoteList.removeSource(_query)
        useCases.disposeUseCases()
    }

    private fun executeNextPageExist(query: Query){
        useCases.nextPageExist.execute(
            onSuccess = {
                _isNextPageExist.postValue(DataState.success(it))
            },
            onError = {
                _isNextPageExist.postValue(DataState.error(it))
            },
            params = query
        )
    }

    fun searchNotes(){
        _mediatorNoteList.postValue(DataState.loading())
        useCases.searchNotes.execute(
                onSuccess = { loadNotes ->
                    totalLoadNotes.addAllUpdate(loadNotes, isBackground = true)
                    executeNextPageExist(nextPageQuery())
                    //checkLastNote(loadSize = loadNotes.size)
                },
                onError = {
                    _mediatorNoteList.postValue(DataState.error(it))
                },
                params = getQuery())
    }

    fun insertNotes(param: NoteView){
        _insertNote.postValue(DataState.loading())
        useCases.insertNewNote.execute(
            onSuccess = {
                _insertNote.postValue(DataState.success(param))
            },
            onError = {
                _insertNote.postValue(DataState.error(it))
            },
            params = param.transNote())
    }

    fun reqUpdateFromDetailFragment(param: NoteView) = when(loadOrderingOnSharedPref()){
        ORDER_DESC -> totalLoadNotes.fetchFirstPositionUpdate(param)
        else -> initNotes()
    }

    fun reqDeleteFromDetailFragment(param: NoteView){
        executeNextPageExist(nextPageQuery(param))
        totalLoadNotes.deleteSync(param, isBackground = false)
    }

    fun deleteNote(param: NoteView){
        _deleteResult.postValue(DataState.loading())
        useCases.deleteNote.execute(
            onSuccess = {},
            onError = {
                _deleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _deleteResult.postValue(DataState.success(param))
                executeNextPageExist(nextPageQuery(param))
                totalLoadNotes.deleteSync(param, isBackground = true)
            },
            params = param.transNote()
        )
    }

    fun nextPageQuery(noteView: NoteView): Query{
        val nextPage = totalLoadNotes.findPage(noteView) + 1
        val startIndex = totalLoadNotes.indexOf(noteView) % getQuery().limit
        return getQuery().copy(
            page = nextPage, startIndex = startIndex
        )
    }

    fun nextPageQuery(): Query{
        val nextPage = getQuery().page + 1
        return getQuery().copy(page = nextPage)
    }

    fun deleteMultiNotes(paramNotes: List<NoteView>){
        _deleteResult.postValue(DataState.loading())
        useCases.deleteMultipleNotes.execute(
            onSuccess = {},
            onError = {
                _deleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _deleteResult.postValue(DataState.success(null))
                initNotes()
            },
            params = paramNotes.transNotes()
        )
    }

    fun setOrdering(ordering: String){
        totalLoadNotes.clear()
        setQuery(
            getQuery().apply {
                page = QUERY_DEFAULT_PAGE
                order = ordering
                startIndex = null
            }
        )
    }

    fun searchKeyword(search: String) {
        totalLoadNotes.clear()
        _query.postValue(getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = loadOrderingOnSharedPref()
            like = search
        })
    }

    fun nextPage() = setQuery(
        getQuery().apply { page += 1 }
    )

    fun setToolbarState(toolbarState: ListToolbarState){
        _toolbarState.value = toolbarState
    }

    private fun getQuery() = _query.value ?: Query(
        order = loadOrderingOnSharedPref()
    )

    private fun clearQuery() = setQuery(
        getQuery()
            .apply {
                page = QUERY_DEFAULT_PAGE
                limit = QUERY_DEFAULT_LIMIT
                sort = SORT_UPDATED_AT
                order = loadOrderingOnSharedPref()
                like = null
                startIndex = null
            }
    )

    private fun setQuery(query: Query){
        _query.value = query
    }

    private fun loadOrderingOnSharedPref() = sharedPreferences
        .getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC

    private fun List<NoteView>.setNoteListSuccessState(isBackground: Boolean){
        if (isBackground)
            _mediatorNoteList.postValue(DataState.success(this))
        else
            _mediatorNoteList.value = DataState.success(this)
    }

    private fun MutableList<NoteView>.addAllUpdate(
        param: List<Note>,
        isBackground: Boolean
    ){
        addAll(param.transNoteViews())
        setNoteListSuccessState(isBackground)
    }

    private fun MutableList<NoteView>.fetchFirstPositionUpdate(param: NoteView){
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
    ){
        remove(target)
        setNoteListSuccessState(isBackground)
    }

    private fun MutableList<NoteView>.deleteSyncCacheNotes(target: NoteView){
        val deletedPage = findPage(target)
        val deletedIndex = indexOf(target) % getQuery().limit
        removeAll(takeTargetToLast(target))
        recallSearchNoteOnDeletedIdx(deletedPage, deletedIndex)
    }

    private fun List<NoteView>.takeTargetToLast(target: NoteView): List<NoteView>{
        val fromLastToDeleteIndex = (lastIndex - indexOf(target)).plus(1)
        return this.takeLast(fromLastToDeleteIndex)
    }

    private fun List<NoteView>.findPage(target: NoteView): Int{
        return (indexOf(target) / getQuery().limit) + 1
    }

    private fun recallSearchNoteOnDeletedIdx(
        deletedPage: Int,
        index: Int
    ){
        setQuery(getQuery()
            .apply {
                page =  deletedPage
                startIndex = index
            }
        )
    }

    fun initNotes() {
        totalLoadNotes.clear()
        clearQuery()
    }

   /* private fun checkLastNote(loadSize: Int){
        _isLastNote = loadSize < getQuery().limit
    }*/
}
