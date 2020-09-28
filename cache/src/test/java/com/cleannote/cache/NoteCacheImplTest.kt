package com.cleannote.cache

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.dao.NoteQueryUtil
import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_ASC
import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_DESC
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.mapper.NoteEntityMapper
import com.cleannote.cache.test.factory.NoteFactory
import com.cleannote.cache.test.factory.QueryFactory
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NoteCacheImplTest {
    // RuntimeEnvironment.application deprecated
    private val database = Room
        .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), NoteDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    private val noteDao = database.noteDao()
    private val entityMapper = NoteEntityMapper()
    private val preferencesHelper = PreferencesHelper(ApplicationProvider.getApplicationContext())

    private val noteCacheImpl = NoteCacheImpl(noteDao, entityMapper, preferencesHelper)


    @Test
    fun insertNotesComplete(){
        val noteEntity = NoteFactory.createNoteEntity("#1","title#1","body#1","10")
        val testObserver = noteCacheImpl.insertCacheNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertNotesSaveData(){
        val insertNum: Int = 10
        val noteEntities = NoteFactory.createNoteEntityList(end = insertNum)

        noteEntities.forEach {
            noteCacheImpl.insertCacheNewNote(it).test()
        }

        checkNoteTableRows(expectedRow = insertNum)
    }

    @Test
    fun searchNoteComplete(){
        val queryEntity = QueryFactory.makeQueryEntity()
        val testObserver = noteCacheImpl.searchNotes(queryEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun searchNoteReturnNotesDESC(){
        val queryEntity = QueryFactory.makeQueryEntity() // default order DESC
        val notes = NoteFactory.createNoteEntityList(end = 5)

        noteCacheImpl.saveNotes(notes)
            .test()


        noteCacheImpl.searchNotes(queryEntity)
            .test()
            .assertComplete()
            .assertValue(notes.asReversed())
    }

    @Test
    fun searchNoteNextPageReturnNotes(){
        val p1Query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val p1Notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(p1Notes).test()

        val p2Query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC, page = 2)
        val p2Notes = NoteFactory.createNoteEntityList(start = 5, end = 10)
        noteCacheImpl.saveNotes(p2Notes).test()

        noteCacheImpl.searchNotes(p1Query)
            .test()
            .assertComplete()
            .assertValue(p1Notes)

        noteCacheImpl.searchNotes(p2Query)
            .test()
            .assertComplete()
            .assertValue(p2Notes)
    }

    @Test
    fun searchNoteLikeQueryReturnSpecificNotes(){
        val like = "title 2"
        val specificQuery = QueryFactory.makeQueryEntity(search = like)
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        val specificNotes = notes.filter { it.title == like }

        noteCacheImpl.searchNotes(specificQuery)
            .test()
            .assertComplete()
            .assertValue(
                specificNotes
            )
    }

    @Test
    fun saveNoteCompleteSaveData(){
        val saveNoteSize = 10
        val noteEntities = NoteFactory.createNoteEntityList(end = saveNoteSize)
        noteCacheImpl.saveNotes(noteEntities).test()
        checkNoteTableRows(saveNoteSize)
    }

    @Test
    fun updateNoteThenSortingTop(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val selectIndex = 2
        val updateTitle = "updated"
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        val updateNote =
            NoteFactory.oneOfNotesUpdate(notes, selectIndex, title= updateTitle, body = null, updateTime = getCurTime())

        noteCacheImpl.updateNote(updateNote).test()
            .assertComplete()
            .assertNoValues()

        val allNotes = noteCacheImpl.searchNotes(query).test().values()[0]

        assertThat(allNotes[notes.lastIndex].title, `is`(updateNote.title))
    }

    @Test
    fun deleteNote(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)

        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        val deleteIndex = 1
        val deleteNote = notes[deleteIndex]

        noteCacheImpl.deleteNote(deleteNote).test()
            .assertComplete()
            .assertNoValues()

        val allNotes = noteCacheImpl.searchNotes(query).test().values()[0]

        assertThat(allNotes, not(hasItem(deleteNote)))
    }

    @Test
    fun deleteMultipleNotes(){
        val query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)

        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        val deleteIndex = 1
        val deleteIndex2 = 3

        val deleteNotes = listOf(notes[deleteIndex], notes[deleteIndex2])
        noteCacheImpl.deleteMultipleNotes(deleteNotes)
            .test()
            .assertComplete()
            .assertNoValues()

        val allNotes = noteCacheImpl.searchNotes(query).test().values()[0]
        assertThat(allNotes, not(hasItems(deleteNotes[0], deleteNotes[1])))
    }

    private fun checkNoteTableRows(expectedRow: Int){
        val numberOfRows = noteDao.getNumNotes().size
        assertThat(numberOfRows, `is`(expectedRow))
    }

    private fun getCurTime() =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())
}