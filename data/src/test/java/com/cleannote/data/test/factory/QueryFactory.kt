package com.cleannote.data.test.factory

import com.cleannote.data.model.QueryEntity
import com.cleannote.domain.model.Query

object QueryFactory {

    fun makeQueryEntity(search: String = "", page: Int = 1) = QueryEntity(
        page, 10, "updated_at", "asc", search, null
    )

    fun makeQuery(search: String = "", page: Int = 1) = Query(
        page, 10, "updated_at", "asc", search, null
    )
}
