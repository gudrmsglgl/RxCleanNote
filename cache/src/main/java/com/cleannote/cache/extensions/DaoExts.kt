package com.cleannote.cache.extensions

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.dao.NoteQueryUtil
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages

fun CachedNoteDao.searchNoteBySorted(
    page: Int,
    limit: Int,
    order: String,
    like: String?
): List<CachedNoteImages> = when(order) {
    NoteQueryUtil.NOTE_SORT_DESC -> searchNotesDESC(page, limit, like ?: "")
    NoteQueryUtil.NOTE_SORT_ASC -> searchNotesASC(page, limit,  like ?: "")
    else -> searchNotesDESC(page, limit,  like ?: "")
}
