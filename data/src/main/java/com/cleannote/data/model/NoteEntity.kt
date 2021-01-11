package com.cleannote.data.model

data class NoteEntity(
    val id: String,
    val title: String,
    val body: String,
    val updatedAt: String,
    val createdAt: String,
    val images: List<NoteImageEntity> ?= null
)