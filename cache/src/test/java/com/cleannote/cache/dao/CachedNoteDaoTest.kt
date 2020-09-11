package com.cleannote.cache.dao

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_ASC
import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_DESC
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.test.factory.NoteFactory
import com.cleannote.cache.test.factory.QueryFactory
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
    fun insertCacheNote(){
        val cacheNote = NoteFactory.createCachedNote(
            "#1", "title#1", "body#1","20")

        noteDao.insertNote(cacheNote)
        val cachedNotes = noteDao.getNumNotes()

        assertThat(cachedNotes.isNotEmpty(), `is`(true))
    }

    @Test
    fun saveNotes(){
        val cacheNotes = NoteFactory.createCachedNoteList(end = 5)
        whenSaveNotes(cacheNotes)

        val allNote = loadAllNotes()
        assertThat(cacheNotes.size, `is`(allNote.size))
    }

    @Test
    fun getCacheNotes(){
        val cachedNotes = NoteFactory.createCachedNoteList(end = 5)

        whenSaveNotes(cachedNotes)

        val retrieveNotes = noteDao.getNumNotes()
        assertThat(retrieveNotes, `is`(cachedNotes))
    }

    @Test
    fun searchNoteASC(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val cacheNotes = NoteFactory.createCachedNoteList(end = 5)

        whenSaveNotes(cacheNotes)

        val searchNotes = noteDao.searchNoteBySorted(query.page, query.limit, query.order, query.like)
        assertThat(searchNotes, `is`(cacheNotes))
    }

    @Test
    fun searchNoteDESC(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)
        val cacheNotes = NoteFactory.createCachedNoteList(end = 5)

        whenSaveNotes(cacheNotes)

        val searchNotes = noteDao.searchNoteBySorted(query.page, query.limit, query.order, query.like)
        assertThat(searchNotes, `is`(cacheNotes.asReversed()))
    }

    @Test
    fun updateNoteThenSortingTopDESC(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)

        val updateTitle = "updateTitle"
        val selectedIndex = 1

        val cacheNotes = NoteFactory.createCachedNoteList(end = 5)
        whenSaveNotes(cacheNotes)

        val updateCacheNote = cacheNotes[selectedIndex]
        updateCacheNote.apply {
            title = updateTitle
            updated_at = getCurTime()
        }
        whenUpdateNote(updateCacheNote)

        val allNotes = noteDao.searchNoteBySorted(query.page, query.limit, query.order, query.like)

        assertThat(allNotes[0], `is`(updateCacheNote))
    }

    @Test
    fun deleteNote(){
        val cacheNotes = NoteFactory.createCachedNoteList(end = 5)
        whenSaveNotes(cacheNotes)
        assertThat(loadAllNotes().size, `is`(cacheNotes.size))

        val deleteIndex = 1
        val deleteNote = cacheNotes[deleteIndex]
        whenDeleteNote(deleteNote)
        assertThat(loadAllNotes(), not(hasItem(deleteNote)))
        assertThat(loadAllNotes().size, `is`(cacheNotes.size - 1))
    }

    private fun whenDeleteNote(note: CachedNote){
        noteDao.deleteNote(note)
    }

    private fun whenSaveNotes(notes: List<CachedNote>) = with(noteDao){
        saveNotes(notes)
    }

    private fun whenUpdateNote(note: CachedNote) {
        noteDao.updateNote(note)
    }

    private fun loadAllNotes() = noteDao.getNumNotes()

    private fun getCurTime() =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())

}