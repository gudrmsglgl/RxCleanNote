package com.cleannote.presentation.notelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.usecases.notelist.GetNumNotes
import com.cleannote.domain.model.Note
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class NoteListViewModel
@Inject constructor(
    private val getNumNotes: GetNumNotes,
    private val noteMapper: NoteMapper
): ViewModel() {

    private val _noteList: MutableLiveData<DataState<List<NoteView>>> = MutableLiveData()
    val noteList: LiveData<DataState<List<NoteView>>>
        get() = _noteList

    private val _insertNote: MutableLiveData<DataState<Long>> = MutableLiveData()
    val insertNote: LiveData<DataState<Long>>
        get() = _insertNote

    private val _usecaseProceed: MediatorLiveData<Boolean> = MediatorLiveData()
    val usecaseProceed: LiveData<Boolean>
        get() = _usecaseProceed

    init {
        fetchNotes()
    }

    override fun onCleared() {
        getNumNotes.dispose()
        super.onCleared()
    }

    fun fetchNotes() {
        _noteList.updateProceed(_usecaseProceed)
        _noteList.postValue(DataState.loading())
        getNumNotes.execute(NoteListSubscriber())
    }

    fun insertNotes(title: String){
        _insertNote.postValue(DataState.loading())
        /*noteListInteractors
            .insertNewNote
            .execute(NoteInsertSubscriber(), noteMapper.mapFromTitle(title))*/
    }

    inner class NoteListSubscriber: DisposableSubscriber<List<Note>>(){
        override fun onComplete() { _usecaseProceed.removeSource(_noteList) }

        override fun onNext(list: List<Note>) {
            _noteList.postValue(DataState.success(
                list.map { noteMapper.mapToView(it) }
            ))
        }

        override fun onError(exception: Throwable){
            _noteList.postValue(DataState.error(
                exception.message
            ))
            _usecaseProceed.removeSource(_noteList)
        }
    }

    inner class NoteInsertSubscriber: DisposableSingleObserver<Long>(){
        override fun onSuccess(t: Long){
            _insertNote.postValue(DataState.success(t))
            _usecaseProceed.removeSource(_insertNote)
        }
        override fun onError(t: Throwable) {
            _insertNote.postValue(DataState.error(t.message))
            _usecaseProceed.removeSource(_insertNote)
        }
    }
}

fun <T> LiveData<DataState<T>>.updateProceed(mediatorLiveData: MediatorLiveData<Boolean>){
    mediatorLiveData.addSource(this){
        when (it.status){
            is State.LOADING -> mediatorLiveData.value = false
            else -> mediatorLiveData.value = true
        }
    }
}