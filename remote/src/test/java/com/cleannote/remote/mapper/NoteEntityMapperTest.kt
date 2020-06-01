package com.cleannote.remote.mapper

import com.cleannote.data.model.NoteEntity
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.test.factory.NoteFactory
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteEntityMapperTest {

    private lateinit var noteEntityMapper: NoteEntityMapper

    @BeforeEach
    fun setUp() {
        noteEntityMapper = NoteEntityMapper()
    }

    @Test
    fun mapFromRemote(){
        val noteModel = NoteFactory.createNoteModel("1", "title#1","body#1")
        val noteEntity = noteEntityMapper.mapFromRemote(noteModel)
        assertModelEqualEntity(noteModel, noteEntity)
    }

    private fun assertModelEqualEntity(model: NoteModel, entity: NoteEntity){
        assertThat(model.title, `is`(entity.title))
        assertThat(model.body, `is`(entity.body))
        assertThat(model.created_at, `is`(entity.created_at))
    }
}