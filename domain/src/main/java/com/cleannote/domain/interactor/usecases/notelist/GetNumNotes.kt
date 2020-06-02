package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.FlowableUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Flowable
import javax.inject.Inject

open class GetNumNotes
@Inject
constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
): FlowableUseCase<List<Note>, Void?>(threadExecutor, postExecutionThread){

    public override fun buildUseCaseFlowable(
        params: Void?
    ): Flowable<List<Note>> = repository.getNumNotes()

}