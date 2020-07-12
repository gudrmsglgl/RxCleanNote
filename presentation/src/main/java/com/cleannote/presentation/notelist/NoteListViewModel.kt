package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
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

    private val query = Query(
        order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
    )

    private val _toolbarState: MutableLiveData<ListToolbarState> = MutableLiveData(SearchState)
    val toolbarState: LiveData<ListToolbarState>
        get() = _toolbarState

    private val _noteList: MutableLiveData<DataState<List<NoteView>>> = MutableLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _noteList

    private val _insertNote: SingleLiveEvent<DataState<NoteView>> = SingleLiveEvent()
    val insertResult: SingleLiveEvent<DataState<NoteView>>
        get() = _insertNote

    override fun onCleared() {
        Log.d("RxCleanNote", "viewModel_onCleared()")
        getNumNotes.dispose()
        searchNotes.dispose()
        insertNewNote.dispose()
        super.onCleared()
    }

    fun fetchNotes() {
        _noteList.postValue(DataState.loading())
        getNumNotes.execute(NoteListSubscriber())
    }

    fun searchNotes(){
        _noteList.postValue(DataState.loading())
        Log.d("RxCleanNote", query.toString())
        searchNotes.execute(NoteListSubscriber(), query)
    }

    fun insertNotes(noteView: NoteView){
        _insertNote.postValue(DataState.loading())
        insertNewNote
            .execute(NoteInsertSubscriber(noteView), noteMapper.mapFromView(noteView))
    }

    fun orderingASC(){
        query.order = ORDER_ASC
        sharedPreferences.edit().putString(FILTER_ORDERING_KEY, ORDER_ASC).apply()
    }

    fun orderingDESC(){
        query.order = ORDER_DESC
        sharedPreferences.edit().putString(FILTER_ORDERING_KEY, ORDER_DESC).apply()
    }

    fun searchKeyword(search: String) = query.apply {
        this.like = search
    }

    fun nextPage(){
        _noteList.value?.data?.let {
            if (it.size == query.limit){
                query.apply { this.page += 1 }
                searchNotes()
            }
        }
    }

    fun clearQuery() = query.apply {
        page = QUERY_DEFAULT_PAGE
        limit = QUERY_DEFAULT_LIMIT
        sort = SORT_UPDATED_AT
        order = sharedPreferences.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC
        like  = null
    }

    inner class NoteListSubscriber: DisposableSubscriber<List<Note>>(){
        override fun onComplete() { }

        override fun onNext(list: List<Note>) {
            _noteList.postValue(DataState.success(
                list.map { noteMapper.mapToView(it) }
            ))
        }

        override fun onError(exception: Throwable){
            _noteList.postValue(DataState.error(
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
}
