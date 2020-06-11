package com.cleannote.cache.mapper

import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.test.factory.NoteFactory
import com.cleannote.data.model.NoteEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class NoteEntityMapperTest {

    private lateinit var noteEntityMapper: NoteEntityMapper

    @Before
    fun setUp() {
        noteEntityMapper = NoteEntityMapper()
    }

    @Test
    fun mapToCachedMapsData(){
        val noteEntity = NoteFactory.createNoteEntity("#1","title#1","body#1")
        val cachedNote = noteEntityMapper.mapToCached(noteEntity)

        assertNoteDataEquality(noteEntity, cachedNote)
    }

    @Test
    fun mapFromCachedMapsData(){
        val cachedNote = NoteFactory.createCachedNote("#1","title#1","body#1")
        val noteEntity = noteEntityMapper.mapFromCached(cachedNote)

        assertNoteDataEquality(noteEntity, cachedNote)
    }

    private fun assertNoteDataEquality(entity: NoteEntity, cache: CachedNote) {
        assertThat(entity.title, `is`(cache.title))
        assertThat(entity.body, `is`(cache.body))
        assertThat(entity.id, `is`(cache.id))
    }
}