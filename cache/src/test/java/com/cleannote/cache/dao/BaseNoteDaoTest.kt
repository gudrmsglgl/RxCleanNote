package com.cleannote.cache.dao

import com.cleannote.cache.extensions.currentNoteSize
import com.cleannote.cache.extensions.searchNoteBySorted
import com.cleannote.cache.model.CachedImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity

open class BaseNoteDaoTest {
    lateinit var noteDao: CachedNoteDao

    protected fun whenInsertNote(param: CachedNote): Long{
        return noteDao.insertNote(param)
    }

    protected fun whenInsertNoteAndImages(param: NoteEntity): Long{
        return noteDao.insertNoteAndImages(param)
    }

    protected fun whenSaveNotesAndImages(noteEntities: List<NoteEntity>){
        noteDao.saveNoteAndImages(noteEntities)
    }

    protected fun whenSearchNotesBySorted(query: QueryEntity): List<CachedNoteImages>{
        return noteDao.searchNoteBySorted(query)
    }

    protected fun whenUpdateNoteImages(updateNoteEntity: NoteEntity) {
        noteDao.updateNoteAndImages(updateNoteEntity)
    }

    protected fun whenDeleteMultipleNotes(notes: List<CachedNote>){
        noteDao.deleteMultipleNotes(notes)
    }

    protected fun whenDeleteNote(note: CachedNote){
        noteDao.deleteNote(note)
    }

    protected fun whenCurrentPageNoteSize(query: QueryEntity): Int{
        return noteDao.currentNoteSize(query)
    }

    protected fun loadCacheNote(pk: String): CachedNote {
        return noteDao.loadNoteByPk(pk)
    }

    protected fun loadImages(pk: String): List<CachedImage>{
        return noteDao.loadImagesByPk(pk)
    }

    protected fun loadNoteAndImages(pk: String): CachedNoteImages {
        return noteDao.loadNoteAndImagesByPk(pk)
    }

    protected fun loadAllCacheNoteAndImages() = noteDao.loadAllCacheNoteAndImages()

}