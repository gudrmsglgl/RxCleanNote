package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notedetail.DeleteNote
import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Query
import com.cleannote.presentation.common.BaseViewModel
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
    private val searchNotes: SearchNotes,
    private val insertNewNote: InsertNewNote,
    private val deleteNote: DeleteNote,
    private val deleteMultipleNotes: DeleteMultipleNotes,
    private val sharedPreferences: SharedPreferences
): BaseViewModel(searchNotes, insertNewNote, deleteNote, deleteMultipleNotes) {

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
        super.onCleared()
    }

    fun searchNotes(){
        _mediatorNoteList.postValue(DataState.loading())
        searchNotes.execute(
                onSuccess = { notes ->
                    loadedNotes.addAll( notes.transNoteViews() )
                    _mediatorNoteList.postValue(DataState.success(loadedNotes))
                },
                onError = {
                    _mediatorNoteList.postValue(DataState.error(it))
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
                _insertNote.postValue(DataState.error(it))
            },
            params = noteView.transNote())
    }

    fun notifyUpdatedNote(updateNoteView: NoteView){
        // step1) find UpdateNoteView
        val findNoteView = loadedNotes.find { it.id == updateNoteView.id }

        // step2) replace updateNoteView
        // DESC -> replace pos 0 NoteView
        // ASC -> remove pos 0 Add last Note -> very complicated b/c NextPage Notes -> LoadDB
        if (loadOrderingOnSharedPreference() == ORDER_DESC){
            loadedNotes.remove(findNoteView)
            loadedNotes.add(0, updateNoteView)
            _mediatorNoteList.value = DataState.success(loadedNotes)
        }
        else if (loadOrderingOnSharedPreference() == ORDER_ASC){
            clearQuery()
        }
    }

    fun notifyDeletedNote(deletedNoteView: NoteView){
        loadedNotes.remove(deletedNoteView)
        _mediatorNoteList.value = DataState.success(loadedNotes)
    }

    fun deleteNote(deletedNoteView: NoteView){
        _deleteResult.postValue(DataState.loading())
        deleteNote.execute(
            onSuccess = {},
            onError = {
                _deleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _deleteResult.postValue(DataState.success(deletedNoteView))
                loadedNotes.remove(deletedNoteView)
                _mediatorNoteList.postValue(DataState.success(loadedNotes))
            },
            params = deletedNoteView.transNote()
        )
    }

    fun deleteMultiNotes(notes: List<NoteView>){
        _deleteResult.postValue(DataState.loading())
        deleteMultipleNotes.execute(
            onSuccess = {},
            onError = {
                _deleteResult.postValue(DataState.error(it))
            },
            onComplete = {
                _deleteResult.postValue(DataState.success(null))
                notes.forEach {
                    loadedNotes.remove(it)
                }
                _mediatorNoteList.postValue(DataState.success(loadedNotes))
            },
            params = notes.transNotes()
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

    // DB every 10 load Note
    // so load Note less than 10 Then Don't exist next page
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

    fun setToolbarState(toolbarState: ListToolbarState){
        _toolbarState.value = toolbarState
    }

    private fun getQuery() = _query.value ?: Query(
        order = loadOrderingOnSharedPreference()
    )

    private fun loadOrderingOnSharedPreference() = sharedPreferences
        .getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
}
