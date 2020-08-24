package com.cleannote.presentation.notedetail

import androidx.lifecycle.*
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.presentation.R
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.notedetail.NoteTitleState
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.data.notedetail.TextMode.EditDoneMode
import com.cleannote.presentation.data.notedetail.TextMode.EditMode
import com.cleannote.presentation.data.notedetail.DetailToolbarState
import com.cleannote.presentation.data.notedetail.DetailToolbarState.*
import com.cleannote.presentation.mapper.NoteMapper
import com.cleannote.presentation.model.NoteView
import io.reactivex.observers.DisposableObserver
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class NoteDetailViewModel
constructor(
    private val updateNote: UpdateNote,
    private val noteMapper: NoteMapper
): ViewModel() {

    private lateinit var note: NoteView

    private val _updatedNote = MutableLiveData<DataState<NoteView>>()
    val updatedNote: LiveData<DataState<NoteView>>
        get() = _updatedNote

    private val _detailToolbarState: MutableLiveData<DetailToolbarState> = MutableLiveData()
    val detailToolbarState: LiveData<DetailToolbarState>
        get() = _detailToolbarState

    val noteTitleState: LiveData<NoteTitleState>
        get() = Transformations.map(_detailToolbarState){
            if (it is TbExpanded) NtExpanded
            else NtCollapse
        }

    private val _noteMode: MutableLiveData<TextMode> = MutableLiveData(EditDoneMode)
    val noteMode: LiveData<TextMode>
        get() = _noteMode

    fun setToolbarState(state: DetailToolbarState){
        _detailToolbarState.value = state
    }

    fun getNoteTile() = note.title

    fun setNoteMode(mode: TextMode){
        _noteMode.value = mode
        if (mode is EditDoneMode){
            _updatedNote.postValue(DataState.loading())
            updateNote.execute(updateObserver, noteMapper.mapFromView(note))
        }
    }

    fun isEditMode(): Boolean{
        return _noteMode.value is EditMode
    }

    fun getNoteBody() = note.body

    fun setNote(noteView: NoteView){
        note = noteView
    }

    fun deleteNote(noteView: NoteView) {
        note = noteView
    }

    private val updateObserver = object: DisposableSubscriber<Unit>(){
        override fun onComplete() {}

        override fun onNext(t: Unit?) {
            _updatedNote.postValue(DataState.success(note))
        }

        override fun onError(t: Throwable?) {
            _updatedNote.postValue(DataState.error("update note fail"))
        }
    }


}