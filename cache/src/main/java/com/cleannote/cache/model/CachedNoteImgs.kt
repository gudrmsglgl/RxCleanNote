package com.cleannote.cache.model

import androidx.room.Embedded
import androidx.room.Relation

data class CachedNoteImages(
    @Embedded var cachedNote: CachedNote,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_pk"
    )
    var images: List<CacheNoteImage>
)