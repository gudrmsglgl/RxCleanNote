package com.cleannote.injection.module

import com.cleannote.data.NoteDataRepository
import com.cleannote.data.executor.JobExecutor
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class DataModule {

    @ActivityScoped
    @Binds
    abstract fun bindNoteRepository(noteDataRepository: NoteDataRepository): NoteRepository

    @ActivityScoped
    @Binds
    abstract fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor
}