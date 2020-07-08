package com.cleannote.domain.model

data class Query(
    var page: Int = 1,
    var limit: Int = 10,
    var sort: String = "updated_at",
    var order: String = "desc",
    var like: String? = null
)