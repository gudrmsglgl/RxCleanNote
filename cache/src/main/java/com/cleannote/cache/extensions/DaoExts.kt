package com.cleannote.cache.extensions

import com.cleannote.cache.dao.CachedNoteDao
import com.cleannote.cache.dao.NoteQueryUtil
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.QueryEntity

fun CachedNoteDao.searchNoteBySorted(
    query: QueryEntity
): List<CachedNoteImages> = when(query.order) {
    NoteQueryUtil.NOTE_SORT_DESC -> searchNotesDESC(query.page, query.limit, query.like ?: "", query.startIndex ?: 0)
    else -> searchNotesASC(query.page, query.limit, query.like ?: "", query.startIndex ?: 0)
}

fun CachedNoteDao.currentNoteSize(
    queryEntity: QueryEntity
): Int = when (queryEntity.order) {
    NoteQueryUtil.NOTE_SORT_DESC -> currentPageNoteSizeOnDESC(queryEntity.page, queryEntity.limit, queryEntity.startIndex ?: 0)
    else -> currentPageNoteSizeOnASC(queryEntity.page, queryEntity.limit, queryEntity.startIndex ?: 0)
}

fun CachedNoteDao.nextPageIsExist(queryEntity: QueryEntity) = when (queryEntity.order){
    NoteQueryUtil.NOTE_SORT_DESC -> nextPageIsExistOnDESC(queryEntity.page, queryEntity.limit, queryEntity.like ?: "", queryEntity.startIndex ?: 0)
    else -> nextPageIsExistOnASC(queryEntity.page, queryEntity.limit, queryEntity.like ?: "", queryEntity.startIndex ?: 0)
}