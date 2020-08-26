package com.cleannote.domain.interactor.usecases.notedetail

import com.cleannote.domain.interactor.FlowableUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Flowable
import javax.inject.Inject

class UpdateNote
@Inject constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : FlowableUseCase<Unit, Note>(threadExecutor, postExecutionThread) {

    public override fun buildUseCaseFlowable(params: Note?): Flowable<Unit> {
       return repository.updateNote(params!!)
    }
}