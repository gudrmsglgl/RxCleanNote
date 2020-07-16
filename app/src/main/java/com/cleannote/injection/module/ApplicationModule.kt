package com.cleannote.injection.module

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.cleannote.NoteApplication
import com.cleannote.domain.Constants.PREF_NOTE_PACKAGE_NAME
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context) =
        context.getSharedPreferences(PREF_NOTE_PACKAGE_NAME, MODE_PRIVATE)

}