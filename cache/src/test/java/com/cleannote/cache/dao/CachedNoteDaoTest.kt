package com.cleannote.cache.dao

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_ASC
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_DESC
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.extensions.divideCacheNote
import com.cleannote.cache.extensions.divideCacheNoteImages
import com.cleannote.cache.extensions.searchNoteBySorted
import com.cleannote.cache.extensions.transEntity
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.test.factory.NoteFactory
import com.cleannote.cache.test.factory.QueryFactory
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
open class CachedNoteDaoTest{

    private lateinit var noteDatabase: NoteDatabase
    private lateinit var noteDao: CachedNoteDao

    /*@get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()*/

    @Before
    fun setUpDb() {
        //val context = InstrumentationRegistry.getInstrumentation().targetContext
        noteDatabase = Room
            .inMemoryDatabaseBuilder( ApplicationProvider.getApplicationContext(), NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = noteDatabase.noteDao()
    }

    @After
    fun closeDb() {
        noteDatabase.close()
    }

    @Test
    fun insertNoteHasImagesThenSaveCacheNoteHasImages(){
        val insertNote = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)
        noteDao.insertNoteAndImages(insertNote)

        val cachedNotes = noteDao.getNumNotes()
        val cachedNoteImages = noteDao.getNoteImagesByPk(insertNote.id)

        assertThat(cachedNotes.isNotEmpty(), `is`(true))
        assertThat(cachedNotes[0], `is`(insertNote.divideCacheNote()))
        assertThat(cachedNoteImages, `is`(insertNote.divideCacheNoteImages()))
    }

    @Test
    fun insertNoteEmptyImagesThenSaveCacheNoteNoImages(){
        val insertNoteEmptyImages = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 0)
        noteDao.insertNoteAndImages(insertNoteEmptyImages)

        val cachedNoteImages = noteDao.getNoteImagesByPk(insertNoteEmptyImages.id)

        assertThat(cachedNoteImages, `is`(emptyList()))
    }

    @Test
    fun saveNotesAndImagesThenCacheSaveNotesImages(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        val cacheNotes = loadAllCachedNotes()
        assertThat(noteEntities.size, `is`(cacheNotes.size))

        cacheNotes.forEachIndexed { index, cachedNote ->
            assertThat(noteEntities[index].divideCacheNoteImages(), `is`(noteDao.getNoteImagesByPk(cachedNote.id)))
        }
    }

    @Test
    fun searchNoteASC(){
        val queryEntity = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        val cachedNotesAndImages  = searchNotes(queryEntity)
        assertThat(cachedNotesAndImages[0].cachedNote, `is`(noteEntities[0].divideCacheNote()))
        assertThat(cachedNotesAndImages[0].images, `is`(emptyList()))
    }

    @Test
    fun searchNoteDESC(){
        val queryEntity = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)

        whenSaveNotesAndImages(noteEntities)

        val cachedNotesAndImages  = searchNotes(queryEntity)
        assertThat(
            cachedNotesAndImages[0].cachedNote,
            `is`(noteEntities.asReversed()[0].divideCacheNote())
        )
    }

    @Test
    fun updateNoteAndImagesThenModifiedNoteAndImagesOnListTop(){
        val queryEntity = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)
        val updateTitle = "updateTitle"
        val selectedIndex = 1

        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        val updateNoteImageEntity = NoteFactory.createNoteImgEntities(noteEntities[selectedIndex].id, 1)
        val updateNoteEntity = NoteFactory.oneOfNotesUpdate(noteEntities, selectedIndex, updateTitle, null, getCurTime(), updateNoteImageEntity)
        whenUpdateNoteImages(updateNoteEntity)

        val cachedNotesImages = searchNotes(queryEntity)

        assertThat(cachedNotesImages[0].transEntity().images, `is`(updateNoteImageEntity))
    }

    @Test
    fun deleteNote(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        val cachedNotes = loadAllCachedNotes()
        assertThat(cachedNotes.size, `is`(noteEntities.size))

        val deleteIndex = 1
        val deleteNote = cachedNotes[deleteIndex]
        whenDeleteNote(deleteNote)

        val cachedNotesAfterDeleted = loadAllCachedNotes()
        val deletedNoteImages = noteDao.getNoteImagesByPk(deleteNote.id)

        assertThat(cachedNotesAfterDeleted, not(hasItem(deleteNote)))
        assertThat(deletedNoteImages, `is`(emptyList()))
    }

    @Test
    fun deleteMultipleNotes(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 5)
        whenSaveNotesAndImages(noteEntities)

        val cachedNotes = loadAllCachedNotes()
        assertThat(cachedNotes.size, `is`(noteEntities.size))

        val deleteIndex1 = 1
        val deleteIndex2 = 3

        val deleteNotes = listOf(cachedNotes[deleteIndex1], cachedNotes[deleteIndex2])
        whenDeleteMultipleNotes(deleteNotes)
        assertThat(loadAllCachedNotes(), not(hasItems(cachedNotes[deleteIndex1], cachedNotes[deleteIndex2])))
    }

    private fun whenDeleteMultipleNotes(notes: List<CachedNote>){
        noteDao.deleteMultipleNotes(notes)
    }

    private fun whenDeleteNote(note: CachedNote){
        noteDao.deleteNote(note)
    }

    private fun whenSaveNotesAndImages(noteEntities: List<NoteEntity>){
        noteDao.saveNoteAndImages(noteEntities)
    }

    private fun searchNotes(query: QueryEntity) =
        noteDao.searchNoteBySorted(query.page, query.limit, query.order, query.like)


    private fun whenUpdateNoteImages(updateNoteEntity: NoteEntity) {
        noteDao.updateNoteAndImages(updateNoteEntity)
    }

    private fun loadAllCachedNotes() = noteDao.getNumNotes()

    private fun getCurTime() =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())

}