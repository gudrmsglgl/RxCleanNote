package com.cleannote.presentation.notelist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.notelist.ListToolbarState
import com.cleannote.presentation.data.notelist.ListToolbarState.SearchState
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class NoteListViewModel
constructor(
    private val getNumNotes: GetNumNotes,
    private val searchNotes: SearchNotes,
    private val insertNewNote: InsertNewNote,
    private val noteMapper: NoteMapper
): ViewModel() {

    companion object{
        private const val SORT_ASC = "asc"
        private const val SORT_DESC = "desc"
    }

    private val query = Query()

    private val _toolbarState: MutableLiveData<ListToolbarState> = MutableLiveData(SearchState)
    val toolbarState: LiveData<ListToolbarState>
        get() = _toolbarState

    private val _noteList: MutableLiveData<DataState<List<NoteView>>> = MutableLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _noteList

    private val _insertNote: MutableLiveData<DataState<Long>> = MutableLiveData()
    val insertResult: LiveData<DataState<Long>>
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
        insertNewNote.execute(NoteInsertSubscriber(), noteMapper.mapFromView(noteView))
    }

    fun sortingASC() = query.apply {
        this.sort = SORT_ASC
    }

    fun sortingDESC() = query.apply {
        this.sort = SORT_DESC
    }

    fun searchKeyword(search: String) = query.apply {
        this.like = search
    }

    fun nextPage(){
        query.apply { this.page += 1 }
        searchNotes()
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

    inner class NoteInsertSubscriber: DisposableSingleObserver<Long>(){
        override fun onSuccess(t: Long){
            _insertNote.postValue(DataState.success(t))
        }
        override fun onError(t: Throwable) {
            _insertNote.postValue(DataState.error(t.message))
        }
    }
}
