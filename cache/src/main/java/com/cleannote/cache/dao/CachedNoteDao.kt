package com.cleannote.cache.dao

import androidx.room.*
import com.cleannote.cache.extensions.divideCacheNote
import com.cleannote.cache.extensions.divideCacheNoteImages
import com.cleannote.cache.model.CacheNoteImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity
import io.reactivex.Completable

@Dao
abstract class CachedNoteDao {

    @Insert
    abstract fun insertNote(note: CachedNote): Long

    @Transaction
    open fun insertNoteAndImages(note: NoteEntity): Long{
        val insertResult = insertNote(note.divideCacheNote())
        if (note.divideCacheNoteImages().isNotEmpty())
            saveImages(note.divideCacheNoteImages())
        return insertResult
    }

    @Query("SELECT * FROM notes")
    abstract fun getNumNotes(): List<CachedNote>

    @Query("SELECT * FROM note_images WHERE note_pk = :pk")
    abstract fun getNoteImagesByPk(pk: String): List<CacheNoteImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveNotes(noteList: List<CachedNote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveImages(images: List<CacheNoteImage>)

    @Transaction
    open fun saveNoteAndImages(notes: List<NoteEntity>){
        val cacheNotes = notes.map { it.divideCacheNote() }
        saveNotes(cacheNotes)
        notes.forEach {
            saveImages(it.divideCacheNoteImages())
        }
    }

    @Transaction
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
    ): List<CachedNoteImages>

    @Transaction
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
    ): List<CachedNoteImages>

    @Update
    abstract fun updateNote(note: CachedNote)

    @Transaction
    open fun updateNoteAndImages(noteEntity: NoteEntity){
        updateNote(noteEntity.divideCacheNote())
        deleteNoteImagesByNotePk(noteEntity.id)
        if (noteEntity.divideCacheNoteImages().isNotEmpty()){
            saveImages(noteEntity.divideCacheNoteImages())
        }
    }

    @Delete
    abstract fun deleteNote(note: CachedNote)

    @Delete
    abstract fun deleteMultipleNotes(notes: List<CachedNote>)

    @Query("DELETE FROM note_images WHERE note_pk = :notePk")
    abstract fun deleteNoteImagesByNotePk(notePk: String)
}