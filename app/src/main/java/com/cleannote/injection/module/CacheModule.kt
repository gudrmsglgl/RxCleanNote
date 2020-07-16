package com.cleannote.injection.module

import android.content.Context
import androidx.room.Room
import com.cleannote.NoteApplication
import com.cleannote.cache.NoteCacheImpl
import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.database.NoteDatabase.Companion.DATABASE_NAME
import com.cleannote.data.repository.NoteCache
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
abstract class CacheModule {

    companion object {
        @ActivityScoped
        @Provides
        fun provideNoteDb(@ApplicationContext context: Context): NoteDatabase{
            return Room
                .databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        @ActivityScoped
        @Provides
        fun provideCacheNoteDao(db: NoteDatabase): CachedNoteDao{
            return db.noteDao()
        }
    }
    @ActivityScoped
    @Binds
    abstract fun bindNoteCache(noteCacheImpl: NoteCacheImpl): NoteCache

}