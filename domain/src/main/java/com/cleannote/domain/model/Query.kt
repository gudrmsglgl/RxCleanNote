package com.cleannote.domain.model

data class Query(
    val page: Int = 1,
    val limit: Int = 5,
    val sort: String = "updated_at",
    val order: String = "desc",
    val like: String = ""
)