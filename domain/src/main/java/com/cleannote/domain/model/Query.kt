package com.cleannote.domain.model

import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT

data class Query(
    var page: Int = QUERY_DEFAULT_PAGE,
    var limit: Int = QUERY_DEFAULT_LIMIT,
    var sort: String = SORT_UPDATED_AT,
    var order: String = ORDER_DESC,
    var like: String? = null
)