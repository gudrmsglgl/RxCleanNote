package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.SingleLiveEvent
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.notelist.ListToolbarState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber

class NoteListViewModel
constructor(
    private val getNumNotes: GetNumNotes,
    private val searchNotes: SearchNotes,
    private val insertNewNote: InsertNewNote,
    private val noteMapper: NoteMapper,
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    private val _query: MutableLiveData<Query> = MutableLiveData(Query(
        order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
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

        _mediatorNoteList.addSource(_insertNote){
            if (it.status is State.SUCCESS) {
                clearQuery()
            }
        }

        _mediatorNoteList.addSource(_query){
            searchNotes()
        }

    }

    override fun onCleared() {
        getNumNotes.dispose()
        searchNotes.dispose()
        insertNewNote.dispose()
        _mediatorNoteList.removeSource(_insertNote)
        _mediatorNoteList.removeSource(_query)
        super.onCleared()
    }

    fun searchNotes(){
        Log.d("RxCleanNote", "searchNotes getQuery: ${getQuery()}")

        _mediatorNoteList.postValue(DataState.loading())
        searchNotes.execute(NoteListSubscriber(), getQuery())
    }

    fun insertNotes(noteView: NoteView){
        _insertNote.postValue(DataState.loading())
        insertNewNote
            .execute(NoteInsertSubscriber(noteView), noteMapper.mapFromView(noteView))
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
            order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
            like = search
        })
    }

    fun nextPage(){
        Log.d("RxCleanNote", "nextPage In")
        _query.value = getQuery().apply { page += 1 }
    }

    fun isExistNextPage(): Boolean = loadedNotes.size / (getQuery().limit) == getQuery().page


    fun clearQuery() {
        loadedNotes.clear()
        _query.value = getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
            like  = null
        }
    }

    inner class NoteListSubscriber: DisposableSubscriber<List<Note>>(){
        override fun onComplete() { }

        override fun onNext(list: List<Note>) {
            loadedNotes.addAll(list.map { noteMapper.mapToView(it) })
            _mediatorNoteList.postValue(DataState.success(loadedNotes))
        }

        override fun onError(exception: Throwable){
            _mediatorNoteList.postValue(DataState.error(
                exception.message
            ))
        }
    }

    inner class NoteInsertSubscriber(private val noteView: NoteView)
        : DisposableSingleObserver<Long>()
    {
        override fun onSuccess(t: Long){
            _insertNote.postValue(DataState.success(noteView))
        }
        override fun onError(t: Throwable) {
            _insertNote.postValue(DataState.error(t.message))
        }
    }

    private fun getQuery() = _query.value ?: Query(
        order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
    )

}
