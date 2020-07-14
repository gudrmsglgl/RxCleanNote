package com.cleannote.presentation.test.factory

import com.cleannote.domain.model.Query

object QueryFactory {

    fun makeQuery(search: String = "", page: Int = 1) = Query(
        page,10,"updated_at","desc", search
    )
}