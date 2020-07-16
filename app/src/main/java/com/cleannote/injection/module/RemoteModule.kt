package com.cleannote.injection.module

import com.cleannote.app.BuildConfig
import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.NoteRemoteImpl
import com.cleannote.remote.NoteService
import com.cleannote.remote.NoteServiceFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class RemoteModule {

    companion object{
        @ActivityScoped
        @Provides
        fun provideNoteService(): NoteService{
            return NoteServiceFactory.makeNoteService(BuildConfig.DEBUG)
        }
    }

    @ActivityScoped
    @Binds
    abstract fun bindNoteRemote(noteRemoteImpl: NoteRemoteImpl): NoteRemote
}