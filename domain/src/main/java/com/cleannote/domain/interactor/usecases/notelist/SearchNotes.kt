package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.FlowableUseCase
import com.cleannote.domain.interactor.SingleUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

open class SearchNotes @Inject constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
): SingleUseCase<List<Note>, Query>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseSingle(params: Query?): Single<List<Note>> {
        return repository.searchNotes(params!!)
    }

}