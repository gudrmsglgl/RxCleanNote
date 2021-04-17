package com.cleannote.domain.model

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val updatedAt: String,
    val createdAt: String,
    val images: List<NoteImage>? = null
)
