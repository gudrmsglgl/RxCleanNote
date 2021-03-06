package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.SingleUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Query
import io.reactivex.Single
import javax.inject.Inject

class NextPageExist
@Inject
constructor(
    val repository: NoteRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : SingleUseCase<Boolean, Query>(threadExecutor, postExecutionThread) {
    override fun buildUseCaseSingle(params: Query?): Single<Boolean> =
        repository.nextPageExist(params!!)
}
