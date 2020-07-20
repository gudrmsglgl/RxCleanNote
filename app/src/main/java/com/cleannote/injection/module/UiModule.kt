package com.cleannote.injection.module

import com.cleannote.UiThread
import com.cleannote.domain.interactor.executor.PostExecutionThread
import dagger.Binds
import dagger.Module

@Module
abstract class UiModule {
    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread
}