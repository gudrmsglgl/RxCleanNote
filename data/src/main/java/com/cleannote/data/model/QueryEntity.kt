package com.cleannote.data.model

data class QueryEntity (
    val page: Int,
    val limit: Int,
    val sort: String,
    val order: String,
    val like: String?
)