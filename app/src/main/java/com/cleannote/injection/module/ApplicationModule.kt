package com.cleannote.injection.module

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.cleannote.NoteApplication
import com.cleannote.domain.Constants.PREF_NOTE_PACKAGE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class ApplicationModule {

    @Binds
    abstract fun bindContext(application: NoteApplication): Context

    @Module
    companion object{

        @Singleton
        @Provides
        @JvmStatic
        fun provideSharedPreferences(context: Context) =
            context.getSharedPreferences(PREF_NOTE_PACKAGE_NAME, MODE_PRIVATE)

    }

}