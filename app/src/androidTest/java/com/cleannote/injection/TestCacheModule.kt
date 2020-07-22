package com.cleannote.injection

import android.content.Context
import androidx.room.Room
import com.cleannote.NoteApplication
import com.cleannote.TestBaseApplication
import com.cleannote.cache.NoteCacheImpl
import com.cleannote.cache.PreferencesHelper
import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.data.repository.NoteCache
import com.cleannote.domain.Constants
import com.nhaarman.mockitokotlin2.mock
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestCacheModule {

    @JvmStatic
    @Provides
    fun provideNoteDb(app: TestBaseApplication): NoteDatabase {
        return Room
            .databaseBuilder(app, NoteDatabase::class.java, NoteDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Provides
    fun provideCacheNoteDao(db: NoteDatabase): CachedNoteDao {
        return db.noteDao()
    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideNoteCache(): NoteCache = mock()

    @Provides
    @JvmStatic
    @Singleton
    fun providePreferencesHelper(): PreferencesHelper = mock()

}