package com.cleannote.injection

import com.cleannote.data.NoteDataRepository
import com.cleannote.data.executor.JobExecutor
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.nhaarman.mockitokotlin2.mock
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class TestDataModule {

    @Module
    companion object{
        @Provides
        @JvmStatic
        @Singleton
        fun provideNoteRepository(): NoteRepository = mock()
    }


    @Binds
    abstract fun bindThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor

}