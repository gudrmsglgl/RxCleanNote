package com.cleannote.injection.module

import com.cleannote.data.repository.NoteRemote
import com.cleannote.remote.NoteRemoteImpl
import com.cleannote.remote.NoteServiceFactory
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteModule {

    @Module
    companion object{

        @JvmStatic
        @Provides
        fun provideNoteService(){
            return NoteServiceFactory.makeNoteService(BuildConfig.DEBUG)
        }
    }

    @Binds
    abstract fun bindNoteRemote(noteRemoteImpl: NoteRemoteImpl): NoteRemote
}