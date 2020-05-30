package com.cleannote.cache

import android.os.Build
import androidx.room.Room
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.mapper.NoteEntityMapper
import com.cleannote.cache.test.factory.NoteFactory
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NoteCacheImplTest {

    private val database = Room
        .inMemoryDatabaseBuilder(RuntimeEnvironment.application, NoteDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    private val noteDao = database.noteDao()
    private val entityMapper = NoteEntityMapper()

    private val noteCacheImpl = NoteCacheImpl(noteDao, entityMapper)

    @Test
    fun getNumNotesComplete(){
        val testObserver = noteCacheImpl.getNumNotes().test()
        testObserver.assertComplete()
    }

    @Test
    fun insertNotesComplete(){
        val noteEntity = NoteFactory.createNoteEntity("#1","title#1","body#1")
        val testObserver = noteCacheImpl.insertCacheNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertNotesSaveData(){
        val insertNum: Int = 10
        val noteEntities = NoteFactory.createNoteEntityList(insertNum)

        noteEntities.forEach {
            noteCacheImpl.insertCacheNewNote(it).test()
        }

        checkNoteTableRows(expectedRow = insertNum)
    }

    private fun checkNoteTableRows(expectedRow: Int){
        val numberOfRows = noteDao.getNumNotes().size
        assertThat(numberOfRows, `is`(expectedRow))
    }
}