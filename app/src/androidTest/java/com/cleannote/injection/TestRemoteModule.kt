package com.cleannote.injection

import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.NoteService
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
object TestRemoteModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideNoteService(): NoteService = mockk()

    @Provides
    @JvmStatic
    @Singleton
    fun provideNoteRemote(): NoteRemote = mockk()
}
