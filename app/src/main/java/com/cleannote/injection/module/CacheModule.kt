package com.cleannote.injection.module

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

@Module
abstract class CacheModule {

    @Module
    companion object{

        @JvmStatic
        @Provides
        fun provideNoteDb(app: NoteApplication): NoteDatabase{
            return Room
                .databaseBuilder(app, NoteDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        @JvmStatic
        @Provides
        fun provideCacheNoteDao(db: NoteDatabase): CachedNoteDao{
            return db.noteDao()
        }

    }

    @Binds
    abstract fun bindNoteCache(noteCacheImpl: NoteCacheImpl): NoteCache

}