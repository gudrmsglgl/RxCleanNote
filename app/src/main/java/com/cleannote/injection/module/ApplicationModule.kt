package com.cleannote.injection.module

import android.app.Application
import android.content.Context
import com.cleannote.NoteApplication
import dagger.Binds
import dagger.Module

@Module
abstract class ApplicationModule {

    @Binds
    abstract fun bindContext(application: NoteApplication): Context
}