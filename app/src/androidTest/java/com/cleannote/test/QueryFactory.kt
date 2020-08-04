package com.cleannote.test

import com.cleannote.domain.Constants
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.model.Query

object QueryFactory {
    fun makeQuery() = Query(
        page = Constants.QUERY_DEFAULT_PAGE ,
        limit = QUERY_DEFAULT_LIMIT,
        sort = Constants.SORT_UPDATED_AT,
        order = Constants.ORDER_ASC,
        like  = null
    )
}