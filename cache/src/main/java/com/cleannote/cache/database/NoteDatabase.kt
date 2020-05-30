package com.cleannote.cache.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.model.CachedNote
import javax.inject.Inject

@Database(entities = [CachedNote::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): CachedNoteDao
    private var INSTANCE: NoteDatabase? = null

    private val sLock = Any()

    fun getInstance(context: Context): NoteDatabase {
        if (INSTANCE == null) {
            synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        NoteDatabase::class.java, DATABASE_NAME)
                        .build()
                }
                return INSTANCE!!
            }
        }
        return INSTANCE!!
    }

    companion object{
        val DATABASE_NAME = "note.db"
    }
}