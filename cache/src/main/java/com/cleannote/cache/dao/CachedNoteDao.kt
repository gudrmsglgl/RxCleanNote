package com.cleannote.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cleannote.cache.model.CachedNote

@Dao
abstract class CachedNoteDao {

    @Insert
    abstract fun insertNote(note: CachedNote): Long

    @Query("SELECT * FROM notes")
    abstract fun getNumNotes(): List<CachedNote>

}