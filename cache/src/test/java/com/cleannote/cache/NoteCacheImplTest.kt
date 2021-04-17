
package com.cleannote.cache

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_ASC
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_DESC
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.test.factory.NoteFactory
import com.cleannote.cache.test.factory.QueryFactory
import com.cleannote.data.model.NoteEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.IndexOutOfBoundsException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NoteCacheImplTest {

    private lateinit var database: NoteDatabase
    private lateinit var noteCacheImpl: NoteCacheImpl

    @Before
    fun cacheImplSetup() {
        database = Room
            .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val noteDao = database.noteDao()
        val preferencesHelper = PreferencesHelper(ApplicationProvider.getApplicationContext())
        noteCacheImpl = NoteCacheImpl(noteDao, preferencesHelper)
    }

    @After
    fun releaseDB() {
        database.close()
    }

    @Test
    fun insertCacheNewNoteComplete() {
        val insertNoteEntity = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)
        noteCacheImpl.insertCacheNewNote(insertNoteEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun insertCacheNewNoteReturnRow() {
        val insertNoteEntity = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)
        noteCacheImpl.insertCacheNewNote(insertNoteEntity)
            .test()
            .assertValue(1L)
    }

    @Test
    fun searchNoteComplete() {
        val queryEntity = QueryFactory.makeQueryEntity()
        noteCacheImpl.searchNotes(queryEntity)
            .test()
            .assertComplete()
    }

    @Test
    fun searchNoteOnCacheEmptyReturnEmpty() {
        val queryEntity = QueryFactory.makeQueryEntity()
        noteCacheImpl.searchNotes(queryEntity)
            .test()
            .assertValue(emptyList())
    }

    @Test
    fun searchNotesOnDescReturnNotesDESC() {
        val queryDesc = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC) // default order DESC
        val notes = NoteFactory.createNoteEntityList(end = 5)

        noteCacheImpl.saveNotes(notes)
            .test()

        noteCacheImpl.searchNotes(queryDesc)
            .test()
            .assertValue(notes.asReversed())
    }

    @Test
    fun searchNotesOnAscReturnNotesASC() {
        val queryDesc = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC) // default order DESC
        val notes = NoteFactory.createNoteEntityList(end = 5)

        noteCacheImpl.saveNotes(notes)
            .test()

        noteCacheImpl.searchNotes(queryDesc)
            .test()
            .assertValue(notes)
    }

    @Test
    fun searchNotesNextPageReturnNextNotes() {
        val p1Query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val p1Notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(p1Notes).test()

        val p2Query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC, page = 2)
        val p2Notes = NoteFactory.createNoteEntityList(start = 5, end = 8)
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
    fun searchNotesLikeQueryOnCacheNotEmptyThenReturnSpecificNotes() {
        val like = "title 2"
        val specificQuery = QueryFactory.makeQueryEntity(search = like)
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        val specificNotes = notes.filter { it.title == like }

        noteCacheImpl.searchNotes(specificQuery)
            .test()
            .assertComplete()
            .assertValue(specificNotes)
    }

    @Test
    fun searchNotesLikeQueryOnCacheEmptyThenReturnEmpty() {
        val like = "i'm android"
        val specificQuery = QueryFactory.makeQueryEntity(search = like)
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes).test()

        noteCacheImpl.searchNotes(specificQuery)
            .test()
            .assertComplete()
            .assertValue(emptyList())
    }

    @Test
    fun updateNoteOnCacheExistNoteThenComplete() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        val updatedNote = existNoteUpdate(
            TempUpdateParam.apply {
                existNotes = notes
                updateIndex = 1
                imageSize = 2
            }
        )

        noteCacheImpl.updateNote(updatedNote)
            .test()
            .assertComplete()
    }

    @Test
    fun updateNoteOnCacheEmptyNoteThenNotComplete() {
        val notes = NoteFactory.createNoteEntityList(end = 5)

        val updatedNote = existNoteUpdate(
            TempUpdateParam.apply {
                existNotes = notes
                updateIndex = 1
                imageSize = 2
            }
        )

        noteCacheImpl.updateNote(updatedNote)
            .test()
            .assertNotComplete()
    }

    @Test
    fun updateNoteReturnNoValue() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        val updatedNote = existNoteUpdate(
            TempUpdateParam.apply {
                existNotes = notes
                updateIndex = 1
                imageSize = 2
            }
        )

        noteCacheImpl.updateNote(updatedNote)
            .test()
            .assertNoValues()
    }

    @Test
    fun deleteNoteOnCacheExistNoteThenComplete() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        noteCacheImpl.deleteNote(notes[1])
            .test()
            .assertComplete()
    }

    @Test
    fun deleteNoteOnCacheNotExistPkThenComplete() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        val notExistNotePk =
            NoteFactory.createNoteEntity(id = "#11", title = "notExistPkNote", date = getCurTime(), imgSize = 1)

        noteCacheImpl.deleteNote(notExistNotePk)
            .test()
            .assertComplete()
    }

    @Test
    fun deleteNoteReturnNoValue() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        noteCacheImpl.deleteNote(notes[1])
            .test()
            .assertNoValues()
    }

    @Test
    fun deleteMultipleNotesComplete() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        val selectedNotes = selectedNoteEntity(notes, 1, 3)
        noteCacheImpl.deleteMultipleNotes(selectedNotes)
            .test()
            .assertComplete()
    }

    @Test
    fun deleteMultipleNotesThenOnCacheRemoved() {
        val notes = NoteFactory.createNoteEntityList(end = 5)
        noteCacheImpl.saveNotes(notes)
            .test()

        val selectedNotes = selectedNoteEntity(notes, 1, 3)
        noteCacheImpl.deleteMultipleNotes(selectedNotes)
            .test()
            .assertComplete()

        noteCacheImpl.searchNotes(QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC))
            .test()
            .assertValue(notes.filterNot { selectedNotes.contains(it) })
    }

    @Test
    fun currentNoteSizeComplete() {
        val p1Query = QueryFactory.makeQueryEntity()
        noteCacheImpl.currentPageNoteSize(p1Query)
            .test()
            .assertComplete()
    }

    @Test
    fun currentNoteSizeOnCacheEmptyThenReturnZero() {
        val p1Query = QueryFactory.makeQueryEntity()
        noteCacheImpl.currentPageNoteSize(p1Query)
            .test()
            .assertValue(0)
    }

    @Test
    fun currentNoteSizeOnCacheNotEmptyThenCurrentPageNoteSize() {
        val p1Query = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val savedNote = NoteFactory.createNoteEntityList(end = 3)

        noteCacheImpl.saveNotes(savedNote)
            .andThen(
                noteCacheImpl.currentPageNoteSize(p1Query)
            )
            .test()
            .assertValue(savedNote.size)
    }

    private fun existNoteUpdate(param: TempUpdateParam): NoteEntity {
        val images = NoteFactory.createNoteImgEntities(param.updateId, param.imageSize)
        return NoteFactory
            .oneOfNotesUpdate(
                param.existNotes, param.updateIndex,
                title = param.updateTitle, body = null,
                updateTime = getCurTime(), updateImages = images
            )
    }

    private fun selectedNoteEntity(savedNotes: List<NoteEntity>, vararg index: Int): List<NoteEntity> {
        val result = mutableListOf<NoteEntity>()
        index.forEach {
            if (it > savedNotes.lastIndex) IndexOutOfBoundsException("저장된 마지막 인덱스의 범위를 초과 했습니다.")
            result.add(savedNotes[it])
        }
        return result
    }

    private fun getCurTime() =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())

    object TempUpdateParam {
        val updateId get() = existNotes[updateIndex].id
        var existNotes: List<NoteEntity> = listOf()
        var updateTitle: String = ""
        var updateIndex: Int = 0
        var imageSize: Int = 0
    }
}
