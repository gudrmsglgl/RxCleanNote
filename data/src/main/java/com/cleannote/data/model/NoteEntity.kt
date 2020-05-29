package com.cleannote.data.model

data class NoteEntity(
    val id: String,
    val title: String,
    val body: String,
    val updated_at: String,
    val created_at: String
)