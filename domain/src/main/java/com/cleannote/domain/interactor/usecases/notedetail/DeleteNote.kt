package com.cleannote.domain.interactor.usecases.notedetail

import com.cleannote.domain.interactor.CompletableUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import io.reactivex.Completable
import javax.inject.Inject

class DeleteNote
@Inject constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
): CompletableUseCase<Note>(threadExecutor, postExecutionThread) {

    public override fun buildUseCaseCompletable(params: Note?): Completable {
        return repository.deleteNote(params!!)
    }
}