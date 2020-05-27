package com.cleannote.domain.model

data class Note(
    val id: String,
    val title: String,
    val body: String,
    val updated_at: String,
    val created_at: String
)