package com.cleannote.injection.module

import com.cleannote.data.NoteDataRepository
import com.cleannote.data.executor.JobExecutor
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {

    @Binds
    abstract fun bindNoteRepository(noteDataRepository: NoteDataRepository): NoteRepository

    @Binds
    abstract fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor
}
