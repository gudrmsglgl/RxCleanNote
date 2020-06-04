package com.cleannote.presentation.test.factory

import com.cleannote.domain.model.Note
import com.cleannote.presentation.model.NoteView
import java.text.SimpleDateFormat
import java.util.*

object NoteFactory {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss")

    private fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }

    fun createNote(
        title: String
    ) = Note(
        id = UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createNoteView(
        title: String
    ) = NoteView(
        id = UUID.randomUUID().toString(),
        title = title,
        body = "",
        created_at = getCurrentTimestamp(),
        updated_at = getCurrentTimestamp()
    )

    fun createNoteList(count: Int): List<Note> = (0 until count).map {
        createNote("title #it") }.toList()

    fun createNoteViewList(count: Int): List<NoteView> = (0 until count).map {
        createNoteView("title #it") }.toList()
}