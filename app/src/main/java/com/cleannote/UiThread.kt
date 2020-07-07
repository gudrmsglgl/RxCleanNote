package com.cleannote

import com.cleannote.domain.interactor.executor.PostExecutionThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class UiThread @Inject internal constructor(): PostExecutionThread
{
    override val scheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
}