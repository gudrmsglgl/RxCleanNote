package com.cleannote.domain.test.factory

import com.cleannote.domain.model.Query

object QueryFactory {

    fun makeDefaultQuery() = Query()

    fun makeSearchQuery(search: String) = Query(like = search)

}