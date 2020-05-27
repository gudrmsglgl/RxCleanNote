package com.cleannote.domain.interfactor.usecases.notelist

import com.cleannote.domain.FlowableUseCase
import com.cleannote.domain.interfactor.executor.PostExecutionThread
import com.cleannote.domain.interfactor.executor.ThreadExecutor
import com.cleannote.domain.interfactor.repository.NoteRepository
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

    public override fun buildUseCaseObservable(
        params: Void?
    ): Flowable<List<Note>> = repository.getNumNotes()

}