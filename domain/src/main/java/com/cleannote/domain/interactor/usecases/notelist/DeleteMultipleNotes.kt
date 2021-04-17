package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.CompletableUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Completable
import javax.inject.Inject

class DeleteMultipleNotes
@Inject
constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<List<Note>>(threadExecutor, postExecutionThread) {
    public override fun buildUseCaseCompletable(params: List<Note>?): Completable {
        return repository.deleteMultipleNotes(params!!)
    }
}
