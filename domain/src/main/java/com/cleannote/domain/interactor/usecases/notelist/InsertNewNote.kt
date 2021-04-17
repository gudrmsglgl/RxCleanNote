package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.SingleUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Single
import javax.inject.Inject

class InsertNewNote
@Inject constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : SingleUseCase<Long, Note>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Note?): Single<Long> = repository.insertNewNote(params!!)
}
