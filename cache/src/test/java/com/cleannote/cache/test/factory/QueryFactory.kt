package com.cleannote.cache.test.factory

import com.cleannote.cache.dao.NoteQueryUtil.NOTE_SORT_DESC
import com.cleannote.data.model.QueryEntity

object QueryFactory {
    fun makeQueryEntity(search: String = "", page: Int = 1, order: String = NOTE_SORT_DESC, limit: Int = 5) = QueryEntity(
        page, limit,"updated_at", order, search, null
    )
}