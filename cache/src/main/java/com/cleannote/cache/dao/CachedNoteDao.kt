package com.cleannote.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleannote.cache.model.CachedNote
import io.reactivex.Completable

@Dao
abstract class CachedNoteDao {

    @Insert
    abstract fun insertNote(note: CachedNote): Long

    @Query("SELECT * FROM notes")
    abstract fun getNumNotes(): List<CachedNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveNotes(noteList: List<CachedNote>)

    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :like || '%' 
        OR body LIKE '%' || :like || '%'
        ORDER BY updated_at DESC LIMIT (:limit) OFFSET ((:page-1) * :limit)
    """)
    abstract fun searchNotesDESC(
        page: Int,
        limit: Int,
        like: String
    ): List<CachedNote>

    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :like || '%' 
        OR body LIKE '%' || :like || '%'
        ORDER BY updated_at ASC LIMIT (:limit) OFFSET ((:page-1) * :limit)
    """)
    abstract fun searchNotesASC(
        page: Int,
        limit: Int,
        like: String
    ): List<CachedNote>
}