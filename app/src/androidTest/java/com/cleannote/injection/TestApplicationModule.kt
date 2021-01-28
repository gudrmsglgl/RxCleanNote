package com.cleannote.injection

import android.content.Context
import android.content.SharedPreferences
import com.cleannote.TestBaseApplication
import com.cleannote.domain.Constants
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import javax.inject.Singleton

@Module
abstract class TestApplicationModule {

    @Binds
    abstract fun bindContext(testApplication: TestBaseApplication): Context

    @Module
    companion object{

        @Singleton
        @Provides
        @JvmStatic
        fun provideSharedPreferences(): SharedPreferences = mockk()

    }
}