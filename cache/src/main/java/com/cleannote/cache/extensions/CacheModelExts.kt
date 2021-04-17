package com.cleannote.cache.extensions

import com.cleannote.cache.model.CachedImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.NoteImageEntity

fun CachedImage.transEntity(): NoteImageEntity = NoteImageEntity(
    this.imgPk, this.notePk, this.imagePath
)

fun NoteImageEntity.transCache(): CachedImage = CachedImage(
    this.imgPk, this.notePk, this.imgPath
)

fun NoteEntity.divideCacheNote(): CachedNote = CachedNote(
    this.id, this.title, this.body, this.updatedAt, this.createdAt
)

fun NoteEntity.divideCacheNoteImages(): List<CachedImage> = this.images?.map { it.transCache() } ?: emptyList()

fun CachedNoteImages.transEntity(): NoteEntity = NoteEntity(
    this.cachedNote.id, this.cachedNote.title, this.cachedNote.body,
    this.cachedNote.updatedAt, this.cachedNote.createdAt,
    this.images.map { it.transEntity() }
)
