package com.cleannote.injection

import androidx.room.Room
import com.cleannote.TestBaseApplication
import com.cleannote.cache.PreferencesHelper
import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.data.repository.NoteCache
import dagger.Module
import dagger.Provides
import io.mockk.mockk
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
    fun provideNoteCache(): NoteCache = mockk()

    @Provides
    @JvmStatic
    @Singleton
    fun providePreferencesHelper(): PreferencesHelper = mockk()
}
