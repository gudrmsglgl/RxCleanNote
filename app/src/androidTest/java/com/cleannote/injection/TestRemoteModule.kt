package com.cleannote.injection

import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.NoteRemoteImpl
import com.cleannote.remote.NoteService
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestRemoteModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideNoteService(): NoteService = mock()

    @Provides
    @JvmStatic
    @Singleton
    fun provideNoteRemote(): NoteRemote = mock()

}