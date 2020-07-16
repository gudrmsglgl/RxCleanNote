package com.cleannote.injection.module

import com.cleannote.UiThread
import com.cleannote.domain.interactor.executor.PostExecutionThread
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class UiModule {
    @ActivityScoped
    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread
}