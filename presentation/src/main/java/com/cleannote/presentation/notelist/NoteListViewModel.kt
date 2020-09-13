package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Query
import com.cleannote.presentation.common.BaseViewModel
import com.cleannote.presentation.common.Constants.FAIL_DELETE_MSG
import com.cleannote.presentation.common.Constants.SUCCESS_DELETE_MSG
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.SingleLiveEvent
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notelist.ListToolbarState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView

class NoteListViewModel
constructor(
    private val searchNotes: SearchNotes,
    private val insertNewNote: InsertNewNote,
    private val deleteNote: DeleteNote,
    private val noteMapper: NoteMapper,
    private val sharedPreferences: SharedPreferences
): BaseViewModel(searchNotes, insertNewNote, deleteNote) {

    private val _query: MutableLiveData<Query> = MutableLiveData(Query(
        order = loadOrderingOnSharedPreference()
    ))

    private val _toolbarState: MutableLiveData<ListToolbarState> = MutableLiveData(SearchState)
    val toolbarState: LiveData<ListToolbarState>
        get() = _toolbarState

    private val loadedNotes = mutableListOf<NoteView>()
    private val _mediatorNoteList: MediatorLiveData<DataState<List<NoteView>>> = MediatorLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _mediatorNoteList

    private val _insertNote: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val insertResult: SingleLiveEvent<DataState<NoteView>>
        get() = _insertNote

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
        super.onCleared()
    }

    fun searchNotes(){
        _mediatorNoteList.postValue(DataState.loading())
        searchNotes.execute(
                onSuccess = { list ->
                    loadedNotes.addAll(list.map { noteMapper.mapToView(it) })
                    _mediatorNoteList.postValue(DataState.success(loadedNotes))
                },
                onError = {
                    _mediatorNoteList.postValue(DataState.error(
                        it.message
                    ))
                },
                params = getQuery())
    }

    fun insertNotes(noteView: NoteView){
        _insertNote.postValue(DataState.loading())
        insertNewNote.execute(
            onSuccess = {
                _insertNote.postValue(DataState.success(noteView))
            },
            onError = {
                _insertNote.postValue(DataState.error(it.message))
            },
            params = noteMapper.mapFromView(noteView))
    }

    fun notifyUpdatedNote(updateNoteView: NoteView){
        with(loadedNotes){
            val updateIndex = indexOfFirst { it.id == updateNoteView.id }
            if (this[updateIndex] == updateNoteView)
                return
            else {
                removeAt(updateIndex)
                if (loadOrderingOnSharedPreference() == ORDER_DESC)
                    add(0, updateNoteView)
                else
                    add(loadedNotes.size, updateNoteView)
            }
        }
        _mediatorNoteList.value = DataState.success(loadedNotes)
    }

    fun notifyDeletedNote(deletedNoteView: NoteView){
        loadedNotes.remove(deletedNoteView)
        _mediatorNoteList.value = DataState.success(loadedNotes)
    }

    fun deleteNote(deletedNoteView: NoteView){
        _mediatorNoteList.postValue(DataState.loading())
        deleteNote.execute(
            onSuccess = {},
            onError = {
                _mediatorNoteList.postValue(DataState.error("삭제에 실패 했습니다."))
            },
            onComplete = {
                loadedNotes.remove(deletedNoteView)
                _mediatorNoteList.postValue(DataState.success(loadedNotes))
            },
            params = noteMapper.mapFromView(deletedNoteView)
        )
    }

    fun setOrdering(ordering: String){
        loadedNotes.clear()
        _query.value = getQuery().apply {
            page = 1
            order = ordering
        }
    }

    fun searchKeyword(search: String) {
        loadedNotes.clear()
        _query.postValue(getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = loadOrderingOnSharedPreference()
            like = search
        })
    }

    fun nextPage(){
        _query.value = getQuery().apply { page += 1 }
    }

    fun isExistNextPage(): Boolean = loadedNotes.size / (getQuery().limit) == getQuery().page


    fun clearQuery() {
        loadedNotes.clear()
        _query.value = getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = loadOrderingOnSharedPreference()
            like  = null
        }
    }

    private fun getQuery() = _query.value ?: Query(
        order = loadOrderingOnSharedPreference()
    )

    private fun loadOrderingOnSharedPreference() = sharedPreferences
        .getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
}
