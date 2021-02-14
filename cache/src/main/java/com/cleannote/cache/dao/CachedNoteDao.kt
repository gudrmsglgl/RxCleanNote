package com.cleannote.cache.dao

import androidx.room.*
import com.cleannote.cache.extensions.divideCacheNote
import com.cleannote.cache.extensions.divideCacheNoteImages
import com.cleannote.cache.model.CachedImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity

@Dao
abstract class CachedNoteDao {

    @Insert
    abstract fun insertNote(note: CachedNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveNotes(noteList: List<CachedNote>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveImages(images: List<CachedImage>)

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
        ORDER BY updated_at DESC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex
    """)
    abstract fun searchNotesDESC(
        page: Int,
        limit: Int,
        like: String,
        startIndex: Int
    ): List<CachedNoteImages>

    @Transaction
    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :like || '%' 
        OR body LIKE '%' || :like || '%'
        ORDER BY updated_at ASC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex
    """)
    abstract fun searchNotesASC(
        page: Int,
        limit: Int,
        like: String,
        startIndex: Int
    ): List<CachedNoteImages>

    @Transaction
    @Query("""
        SELECT EXISTS (
            SELECT * FROM notes
            WHERE title LIKE '%' || :like || '%'
            OR body LIKE '%' || :like || '%'
            ORDER BY updated_at DESC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex
        )
    """)
    abstract fun nextPageIsExistOnDESC(
        page: Int,
        limit: Int,
        like: String,
        startIndex: Int
    ): Boolean

    @Transaction
    @Query("""
        SELECT EXISTS (
            SELECT * FROM notes
            WHERE title LIKE '%' || :like || '%'
            OR body LIKE '%' || :like || '%'
            ORDER BY updated_at ASC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex
        )
    """)
    abstract fun nextPageIsExistOnASC(
        page: Int,
        limit: Int,
        like: String,
        startIndex: Int
    ): Boolean

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

    @Query("SELECT COUNT(*) FROM notes ORDER BY updated_at ASC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex")
    abstract fun currentPageNoteSizeOnASC(page: Int, limit: Int, startIndex: Int): Int

    @Query("SELECT COUNT(*) FROM notes ORDER BY updated_at DESC LIMIT (:limit) OFFSET ((:page-1) * :limit) + :startIndex")
    abstract fun currentPageNoteSizeOnDESC(page: Int, limit: Int, startIndex: Int): Int

    @Transaction
    open fun insertNoteAndImages(note: NoteEntity): Long{
        val insertResult = insertNote(note.divideCacheNote())
        if (!note.images.isNullOrEmpty())
            saveImages(note.divideCacheNoteImages())
        return insertResult
    }
    /*
    *   For DAO Test Func
    * */
    @Transaction
    @Query("SELECT * FROM notes")
    abstract fun loadAllCacheNoteAndImages(): List<CachedNoteImages>

    @Query("SELECT * FROM note_images WHERE note_pk = :pk")
    abstract fun loadImagesByPk(pk: String): List<CachedImage>

    @Query("SELECT * FROM notes WHERE id = :pk")
    abstract fun loadNoteByPk(pk: String): CachedNote

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :pk")
    abstract fun loadNoteAndImagesByPk(pk: String): CachedNoteImages

}