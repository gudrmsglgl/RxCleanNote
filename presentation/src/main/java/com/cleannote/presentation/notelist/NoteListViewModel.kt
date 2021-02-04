package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
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

    private var _isLastNote = false
    val isLastNote get() = _isLastNote

    private val _insertNote: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val insertResult: SingleLiveEvent<DataState<NoteView>>
        get() = _insertNote

    private val _deleteResult: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val deleteResult: SingleLiveEvent<DataState<NoteView>>
        get() = _deleteResult

    init {
        with(_mediatorNoteList) {
            addSource(_insertNote){
                if (it.status is SUCCESS) clearQuery()
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

    fun searchNotes(){
        _mediatorNoteList.postValue(DataState.loading())
        useCases.searchNotes.execute(
                onSuccess = { searchedNotes ->
                    totalLoadNotes
                        .apply {
                            addAll(searchedNotes.transNoteViews())
                            setNoteListSuccessState(isBackground = true)
                        }
                    checkLastNote(searchedNotes.size)
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

    fun reqUpdateFromDetailFragment(param: NoteView){
        if (loadOrderingOnSharedPref() == ORDER_DESC)
            totalLoadNotes
                .apply {
                    val findNoteView = find { it.id == param.id }
                    remove(findNoteView)
                    add(0, param)
                    setNoteListSuccessState(isBackground = false)
                }
        else if (loadOrderingOnSharedPref() == ORDER_ASC)
            clearQuery()
    }

    fun reqDeleteFromDetailFragment(param: NoteView){
        totalLoadNotes.apply {
            remove(param)
            setNoteListSuccessState(isBackground = false)
        }
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
                totalLoadNotes
                    .apply {
                        remove(param)
                        setNoteListSuccessState(isBackground = true)
                    }
            },
            params = param.transNote()
        )
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
                totalLoadNotes
                    .apply {
                        paramNotes.forEach { remove(it) }
                        setNoteListSuccessState(isBackground = true)
                    }
            },
            params = paramNotes.transNotes()
        )
    }

    fun setOrdering(ordering: String){
        totalLoadNotes.clear()
        _query.value = getQuery().apply {
            page = 1
            order = ordering
        }
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

    fun nextPage(){
        _query.value = getQuery().apply { page += 1 }
    }

    // DB every 10 load Note
    // so load Note less than 10 Then Don't exist next page
    fun isExistNextPage(): Boolean{
        val totalNoteSize = totalLoadNotes.size
        val queryLimit = getQuery().limit
        val queryPage = getQuery().page

        Log.d("RxCleanNote", "totalNoteSize: $totalNoteSize / queryLimit: $queryLimit == queryPage: $queryPage")
        return totalLoadNotes.size / (getQuery().limit) == getQuery().page
    }

    fun clearQuery() {
        totalLoadNotes.clear()
        _query.value = getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = loadOrderingOnSharedPref()
            like  = null
        }
    }

    fun setToolbarState(toolbarState: ListToolbarState){
        _toolbarState.value = toolbarState
    }

    private fun getQuery() = _query.value ?: Query(
        order = loadOrderingOnSharedPref()
    )

    private fun loadOrderingOnSharedPref() = sharedPreferences
        .getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC

    private fun List<NoteView>.setNoteListSuccessState(isBackground: Boolean){
        if (isBackground)
            _mediatorNoteList.postValue(DataState.success(this))
        else
            _mediatorNoteList.value = DataState.success(this)
    }

    private fun checkLastNote(loadSize: Int){
        _isLastNote = loadSize < getQuery().limit
    }
}
