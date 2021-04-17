package com.cleannote.presentation.model

data class NoteView(
    val id: String,
    val title: String,
    val body: String,
    val updatedAt: String,
    val createdAt: String,
    val noteImages: List<NoteImageView>? = null
)
