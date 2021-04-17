package com.cleannote.notelist

import com.cleannote.model.NoteUiModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

class SubjectManager @Inject constructor() {

    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject get() = _clickNoteSubject

    private val _longClickSubject: PublishSubject<Unit> = PublishSubject.create()
    val longClickSubject get() = _longClickSubject

    private val _deleteClickSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val deleteClickSubject get() = _deleteClickSubject

    private val _checkNoteSubject: PublishSubject<Pair<NoteUiModel, Boolean>> = PublishSubject.create()
    val checkNoteSubject get() = _checkNoteSubject

    private val subjects: Set<Subject<*>> =
        setOf(_clickNoteSubject, _longClickSubject, _deleteClickSubject, _checkNoteSubject)

    fun releaseSubjects() {
        subjects.forEach {
            it.onComplete()
        }
    }
}
