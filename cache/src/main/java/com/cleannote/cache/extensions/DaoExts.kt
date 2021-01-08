package com.cleannote.cache.extensions

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.dao.NoteQueryUtil
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.QueryEntity

/*fun CachedNoteDao.searchNoteBySorted(
    page: Int,
    limit: Int,
    order: String,
    like: String?
): List<CachedNoteImages> = when(order) {
    NoteQueryUtil.NOTE_SORT_DESC -> searchNotesDESC(page, limit, like ?: "")
    NoteQueryUtil.NOTE_SORT_ASC -> searchNotesASC(page, limit,  like ?: "")
    else -> searchNotesDESC(page, limit,  like ?: "")
}*/

fun CachedNoteDao.searchNoteBySorted(
    query: QueryEntity
): List<CachedNoteImages> = when(query.order) {
    NoteQueryUtil.NOTE_SORT_DESC -> searchNotesDESC(query.page, query.limit, query.like ?: "")
    else -> searchNotesASC(query.page, query.limit, query.like ?: "")
}


fun CachedNoteDao.currentNoteSize(
    queryEntity: QueryEntity
): Int = when (queryEntity.order) {
    NoteQueryUtil.NOTE_SORT_DESC -> currentPageNoteSizeOnDESC(queryEntity.page, queryEntity.limit)
    else -> currentPageNoteSizeOnASC(queryEntity.page, queryEntity.limit)
}