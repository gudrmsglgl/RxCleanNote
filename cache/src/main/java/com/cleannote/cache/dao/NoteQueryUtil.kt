package com.cleannote.cache.dao

import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_DESC
import com.cleannote.cache.model.CachedNote

class NoteQueryUtil {

    companion object {
        const val NOTE_SORT_ASC = "asc"
        const val NOTE_SORT_DESC = "desc"
    }
}

fun CachedNoteDao.searchNoteBySorted(
    page: Int,
    limit: Int,
    order: String,
    sort: String,
    like: String?
): List<CachedNote> = when {
    sort == NOTE_SORT_DESC -> searchNotesDESC(page, limit, order, like ?: "")
    else -> searchNotesASC(page, limit, order, like ?: "")
}
