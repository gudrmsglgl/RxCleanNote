package com.cleannote.remote.test.factory

import com.cleannote.data.model.QueryEntity

object QueryFactory {
    fun makeQueryEntity(search: String = "", page: Int = 1) = QueryEntity(
        page, 10, "updated_at", "desc", search, null
    )
}
