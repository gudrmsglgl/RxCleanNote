package com.cleannote.domain.interfactor.usecases.notelist

import com.cleannote.domain.interfactor.SingleUseCase
import com.cleannote.domain.interfactor.executor.PostExecutionThread
import com.cleannote.domain.interfactor.executor.ThreadExecutor
import com.cleannote.domain.interfactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Single
import javax.inject.Inject

class InsertNewNoteUseCase
@Inject constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
): SingleUseCase<Long, Note>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Note?): Single<Long> = repository.insertNewNote(params!!)

}