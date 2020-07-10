package com.cleannote.cache.dao

import com.cleannote.cache.dao.NoteQueryUtil.Companion.NOTE_SORT_ASC
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
    like: String?
): List<CachedNote> = when(order) {
    NOTE_SORT_DESC -> searchNotesDESC(page, limit, like ?: "")
    NOTE_SORT_ASC -> searchNotesASC(page, limit,  like ?: "")
    else -> searchNotesDESC(page, limit,  like ?: "")
}
