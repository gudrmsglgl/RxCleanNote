package com.cleannote.notedetail.edit.factory

import com.cleannote.presentation.model.NoteImageView
import com.cleannote.presentation.model.NoteView
import java.util.*

class NoteFactory {
    companion object {
        fun defaultNote(): NoteView{
            val notePk = UUID.randomUUID().toString()
            return NoteView(
                id = notePk,
                title = "emptyTile",
                body = "emptyBody",
                updatedAt = "2021-10-10",
                createdAt = "2021-10-10",
                noteImages = listOf(NoteImageView(UUID.randomUUID().toString(), notePk, "https://pbs.twimg.com/profile_images/1105831612132945925/Uf2a_wyY_400x400.jpg"))
            )
        }
    }
}