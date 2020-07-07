package com.cleannote.cache.dao

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.test.factory.NoteFactory
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

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
            "#1", "title#1", "body#1")

        noteDao.insertNote(cacheNote)
        val cachedNotes = noteDao.getNumNotes()

        assertThat(cachedNotes.isNotEmpty(), `is`(true))
    }

    @Test
    fun getCacheNotes(){
        val cachedNotes = NoteFactory.createCachedNoteList(5)

        cachedNotes.forEach {
            noteDao.insertNote(it)
        }

        val retrieveNotes = noteDao.getNumNotes()

        assertThat(retrieveNotes, `is`(cachedNotes))
    }
}