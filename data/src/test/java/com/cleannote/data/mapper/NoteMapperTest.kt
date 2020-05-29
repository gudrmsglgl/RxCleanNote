package com.cleannote.data.mapper

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteMapperTest {
    private lateinit var noteMapper: NoteMapper

    @BeforeEach
    fun setUp() {
        noteMapper = NoteMapper()
    }

    @Test
    fun mapFromNoteEntityMapsData() {
        val noteEntity: NoteEntity = NoteFactory.createNoteEntity("1","title #1", "body #1")
        val note: Note = noteMapper.mapFromEntity(noteEntity)
        assertNoteDataEquality(noteEntity, note)
    }

    @Test
    fun mapToEntityMapsData() {
        val cachedNote: Note = NoteFactory.createNote("1","title #1", "body #1")
        val noteEntity: NoteEntity = noteMapper.mapToEntity(cachedNote)

        assertNoteDataEquality(noteEntity, cachedNote)
    }

    private fun assertNoteDataEquality(entity: NoteEntity, domain: Note) {
        assertThat(entity.title, `is`(domain.title))
        assertThat(entity.body, `is`(domain.body))
        assertThat(entity.id, `is`(domain.id))
    }
}