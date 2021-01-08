package com.cleannote.cache.dao

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_ASC
import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_DESC
import com.cleannote.cache.database.NoteDatabase
import com.cleannote.cache.extensions.*
import com.cleannote.cache.model.CachedNote
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
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
open class CachedNoteDaoTest: BaseNoteDaoTest(){

    private lateinit var noteDatabase: NoteDatabase

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
    fun insertNoteThenAssertLong(){
        val note = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)

        whenInsertNote(note.divideCacheNote())
            .assertGreaterThan(0L)
    }

    @Test
    fun insertNoteAndImagesThenAssertLong(){
        val note = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)

        whenInsertNoteAndImages(note)
            .assertGreaterThan(0L)
    }

    @Test
    fun noteOfHasImagesInsertNoteThenNotSaveImages(){
        val note = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)
        val insertedNote = note.divideCacheNote()

        whenInsertNote(note.divideCacheNote())

        loadNoteAndImages(pk = note.id)
            .hasNote(insertedNote)
            .hasImages(emptyList())
    }

    @Test
    fun noteOfHasImagesInsertNoteAndImagesThenSaveImages(){
        val note = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 3)
        val insertedNote = note.divideCacheNote()
        val insertedNoteImages = note.divideCacheNoteImages()

        whenInsertNoteAndImages(note)

        loadNoteAndImages(pk = note.id)
            .hasNote(insertedNote)
            .hasImages(insertedNoteImages)
    }

    @Test
    fun noteOfEmptyImagesInsertNoteAndImagesThenNotSaveImages(){
        val note = NoteFactory.createNoteEntity("#1", "title#1", null, "20", 0)
        val insertedNote = note.divideCacheNote()

        whenInsertNoteAndImages(note)

        loadNoteAndImages(pk = note.id)
            .hasNote(insertedNote)
            .hasImages(emptyList())
    }

    @Test
    fun saveNotesAndImagesThenCacheDataReturnSavedNotes(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        loadAllCacheNoteAndImages()
            .forEachIndexed { index, loadNoteAndImages ->

                val savedNote = noteEntities[index].divideCacheNote()
                val savedImages = noteEntities[index].divideCacheNoteImages()

                loadNoteAndImages
                    .hasNote(savedNote)
                    .hasImages(savedImages)

            }
    }

    @Test
    fun searchNotesBySortedOnQueryASCReturnASCValue(){
        val queryASC = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)
        val savedNotes = NoteFactory.createNoteEntityList(end = 3)

        whenSaveNotesAndImages(savedNotes)

        whenSearchNotesBySorted(queryASC)
            .assertSortedValue(savedNotes)
    }

    @Test
    fun searchNotesBySortedOnQueryDESCReturnDescValue(){
        val queryDESC = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)
        val savedNotes = NoteFactory.createNoteEntityList(end = 3)

        whenSaveNotesAndImages(savedNotes)

        whenSearchNotesBySorted(queryDESC)
            .assertSortedValue(savedNotes.asReversed())
    }

    @Test
    fun updateNoteAndImagesOfHasImagesThenUpdateOtherImagesNote(){
        val existNote = loadExistingNote(index = 1, total = 3)
        val updateNote = update(existNote, imageSize = 5)

        loadNoteAndImages(pk = existNote.id)
            .hasNote(updateNote.divideCacheNote())
            .hasImages(updateNote.divideCacheNoteImages())
    }

    @Test
    fun updateNoteAndImagesOfEmptyImagesThenUpdateEmptyImagesNote(){
        val existNote = loadExistingNote(index = 0, total = 5)
        val updateNote = update(existNote, imageSize = 0)

        loadNoteAndImages(pk = existNote.id)
            .hasNote(updateNote.divideCacheNote())
            .hasImages(emptyList())
    }

    @Test
    fun updateNoteAndImagesThenSearchNotesDescReturnFirstIndex(){
        val queryDesc = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)

        val existNote = loadExistingNote(index = 1, total = 3)
        val updateNote = update(existNote, imageSize = 5)

        whenSearchNotesBySorted(queryDesc)
            .assertValueAt(index = 0, expect = updateNote)
    }

    @Test
    fun updateNoteAndImagesThenSearchNotesASCReturnLastIndex(){
        val queryAsc = QueryFactory.makeQueryEntity(order = NOTE_SORT_ASC)

        val existNote = loadExistingNote(index = 1, total = 3)
        val updateNote = update(existNote, imageSize = 5)

        whenSearchNotesBySorted(queryAsc)
            .assertValueAt(index = loadAllCacheNoteAndImages().lastIndex, expect = updateNote)
    }

    @Test
    fun deleteNoteThenExistNotesRemoved(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)
        whenSaveNotesAndImages(noteEntities)

        val deletedNote = delete(savedNotes = noteEntities, index = 2)

        loadAllCacheNoteAndImages()
            .notHasNote(deletedNote.divideCacheNote())
            .notHasImages(deletedNote.divideCacheNoteImages())

        loadImages(deletedNote.id)
            .expectImages(emptyList())
    }

    @Test
    fun deleteMultipleNotesThenExistNotesRemoved(){
        val noteEntities = NoteFactory.createNoteEntityList(end = 5)
        whenSaveNotesAndImages(noteEntities)

        val deletedNotes = deleteMultiple(noteEntities, 1, 3,4)

        loadAllCacheNoteAndImages()
            .notHasNotes(deletedNotes)
    }

    @Test
    fun currentPageNoteSizeThenReturnCacheNoteSize(){
        val queryDesc = QueryFactory.makeQueryEntity(order = NOTE_SORT_DESC)
        val noteEntities = NoteFactory.createNoteEntityList(end = 3)

        whenSaveNotesAndImages(noteEntities)

        whenCurrentPageNoteSize(queryDesc)
            .expectNoteSize(
                whenSearchNotesBySorted(queryDesc).size
            )
    }

    private fun loadExistingNote(index: Int, total: Int): NoteEntity{
        val savedNote = NoteFactory.createNoteEntityList(end = total)
        whenSaveNotesAndImages(savedNote)
        return savedNote[index]
    }

    private fun update(existNote: NoteEntity, imageSize: Int): NoteEntity{
        val updateId = existNote.id
        val updateImages = NoteFactory.createNoteImgEntities(updateId, imageSize)
        val updatedNote = existNote.copy(title = "updateTitle", updated_at = getCurTime(), images = updateImages)
        whenUpdateNoteImages(updatedNote)
        return updatedNote
    }

    private fun delete(savedNotes: List<NoteEntity>, index: Int): NoteEntity{
        val deletedNote = savedNotes[index].divideCacheNote()
        whenDeleteNote(deletedNote)
        return savedNotes[index]
    }

    private fun deleteMultiple(savedNotes: List<NoteEntity>, vararg indexs: Int): List<CachedNote>{
        val cachedNotes = selectedNoteEntity(savedNotes, indexs).map { it.divideCacheNote() }
        whenDeleteMultipleNotes(cachedNotes)
        return cachedNotes
    }

    private fun selectedNoteEntity(savedNotes: List<NoteEntity>, indexs: IntArray): List<NoteEntity>{
        val result = mutableListOf<NoteEntity>()
        indexs.forEach {
            if (it > savedNotes.lastIndex) IndexOutOfBoundsException("저장된 마지막 인덱스의 범위를 초과 했습니다.")
            result.add(savedNotes[it])
        }
        return result
    }

    private fun getCurTime() =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())

}