package com.cleannote.domain.interactor.usecases.splash

import com.cleannote.domain.interactor.FlowableUseCase
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.User
import io.reactivex.Flowable
import javax.inject.Inject

open class Login @Inject constructor(val repository: NoteRepository,
                                     threadExecutor: ThreadExecutor,
                                     postExecutionThread: PostExecutionThread):
    FlowableUseCase<List<User>, String>(threadExecutor, postExecutionThread) {

    public override fun buildUseCaseFlowable(params: String?): Flowable<List<User>> {
        return repository.login(params!!)
    }

}